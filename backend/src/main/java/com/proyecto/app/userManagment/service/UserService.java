package com.proyecto.app.userManagment.service;

import com.proyecto.app.common.User;
import com.proyecto.app.security.domain.Credential;
import com.proyecto.app.security.dto.request.RegisterRequest;
import com.proyecto.app.security.dto.response.RoleDTO;
import com.proyecto.app.security.service.CredentialService;
import com.proyecto.app.tourManagment.api.TourQueryService;
import com.proyecto.app.tourManagment.dto.response.TourResponse;
import com.proyecto.app.userManagment.domain.*;
import com.proyecto.app.userManagment.dto.request.CreateUserRequest;
import com.proyecto.app.userManagment.dto.request.UpdateContactRequest;
import com.proyecto.app.userManagment.dto.request.UpdateUserRequest;
import com.proyecto.app.userManagment.dto.response.ContactResponse;
import com.proyecto.app.userManagment.dto.response.UserProfileResponse;
import com.proyecto.app.userManagment.dto.response.UserResponse;
import com.proyecto.app.userManagment.repository.ContactRepository;
import com.proyecto.app.userManagment.repository.SavedTourRepository;
import com.proyecto.app.userManagment.repository.UserProfileRepository;
import com.proyecto.app.userManagment.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ContactRepository contactRepository;
    private final CredentialService credentialService;
    private final SavedTourRepository savedTourRepository;
    private final TourQueryService tourQueryService;

    public UserService(UserRepository userRepository,
                       UserProfileRepository userProfileRepository,
                       ContactRepository contactRepository,
                       CredentialService credentialService,
                       SavedTourRepository savedTourRepository,
                       TourQueryService tourQueryService) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.contactRepository = contactRepository;
        this.credentialService = credentialService;
        this.savedTourRepository = savedTourRepository;
        this.tourQueryService = tourQueryService;
    }

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        if (request.getRoleName().equalsIgnoreCase("ADMINISTRATOR")) {
            throw new IllegalArgumentException("No se puede registrar un administrador desde este endpoint.");
        }

        if (userProfileRepository.existsByDocument(request.getDocument())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese documento.");
        }

        if (request.getPhoneNumbers() == null || request.getPhoneNumbers().isEmpty()) {
            throw new IllegalArgumentException("Se requiere al menos un número de contacto.");
        }

        Role role = resolveRole(request.getRoleName());

        UserProfile profile = new UserProfile(
                request.getName(),
                request.getDocument(),
                role.getNameRole()
        );

        request.getPhoneNumbers().forEach(phone -> {
            Contact contact = new Contact(phone, profile);
            profile.addContact(contact);
        });

        userProfileRepository.save(profile);

        User user = new User(profile);
        userRepository.save(user);

        RoleDTO roleDTO = new RoleDTO(role.getNameRole(), role.getPermissions());
        RegisterRequest registerRequest = new RegisterRequest(
                request.getEmail(),
                request.getPassword(),
                user.getId(),
                roleDTO
        );
        Credential credential = credentialService.createCredential(registerRequest);

        return buildUserResponse(user, credential.getEmail());
    }

    // ----------------------------------------------------------------
    // READ
    // ----------------------------------------------------------------

    public UserResponse getProfile(Long userId) {
        User user = findUserById(userId);
        Credential credential = credentialService.getCredentialByUserId(user.getId());
        return buildUserResponse(user, credential.getEmail());
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------

    @Transactional
    public UserResponse updateInformation(Long userId, UpdateUserRequest request) {
        User user = findUserById(userId);
        user.getUserProfile().updateInformation(request.getField(), request.getNewValue());
        userProfileRepository.save(user.getUserProfile());

        Credential credential = credentialService.getCredentialByUserId(user.getId());
        return buildUserResponse(user, credential.getEmail());
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------

    @Transactional
    public void deleteAccount(Long userId) {
        User user = findUserById(userId);
        userRepository.delete(user);
    }

    // ----------------------------------------------------------------
    // CONTACTS
    // ----------------------------------------------------------------

    @Transactional
    public UserResponse addContact(Long userId, UpdateContactRequest request) {
        User user = findUserById(userId);
        Contact contact = new Contact(request.getPhoneNumber(), user.getUserProfile());
        user.getUserProfile().addContact(contact);
        userProfileRepository.save(user.getUserProfile());

        Credential credential = credentialService.getCredentialByUserId(user.getId());
        return buildUserResponse(user, credential.getEmail());
    }

    @Transactional
    public UserResponse updateContact(Long userId, UpdateContactRequest request) {
        User user = findUserById(userId);

        Contact contact = contactRepository.findById(request.getContactId())
                .orElseThrow(() -> new IllegalArgumentException("Contacto no encontrado."));

        if (!contact.getUserProfile().getId().equals(user.getUserProfile().getId())) {
            throw new SecurityException("El contacto no pertenece a este usuario.");
        }

        contact.setPhoneNumber(request.getPhoneNumber());
        contactRepository.save(contact);

        Credential credential = credentialService.getCredentialByUserId(user.getId());
        return buildUserResponse(user, credential.getEmail());
    }

    @Transactional
    public void deleteContact(Long userId, Long contactId) {
        User user = findUserById(userId);

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Contacto no encontrado."));

        if (!contact.getUserProfile().getId().equals(user.getUserProfile().getId())) {
            throw new SecurityException("El contacto no pertenece a este usuario.");
        }

        List<Contact> contacts = contactRepository.findAllByUserProfileId(user.getUserProfile().getId());
        if (contacts.size() <= 1) {
            throw new IllegalStateException("El usuario debe tener al menos un contacto.");
        }

        user.getUserProfile().removeContact(contact);
        contactRepository.delete(contact);
    }

    // ----------------------------------------------------------------
    // SAVED TOURS
    // ----------------------------------------------------------------

    @Transactional
    public void saveTour(Long userId, Long tourId) {
        User user = findUserById(userId);

        if (!tourQueryService.exists(tourId)) {
            throw new IllegalArgumentException("Recorrido no encontrado con id: " + tourId);
        }
        if (savedTourRepository.existsByUserIdAndTourId(userId, tourId)) {
            throw new IllegalStateException("El recorrido ya está guardado.");
        }

        savedTourRepository.save(new SavedTour(user, tourId));
    }

    @Transactional
    public void removeSavedTour(Long userId, Long tourId) {
        findUserById(userId);

        if (!savedTourRepository.existsByUserIdAndTourId(userId, tourId)) {
            throw new IllegalArgumentException("El recorrido no está en la lista de guardados.");
        }

        savedTourRepository.deleteByUserIdAndTourId(userId, tourId);
    }

    public List<TourResponse> getSavedTours(Long userId) {
        findUserById(userId);

        List<Long> tourIds = savedTourRepository.findAllByUserId(userId).stream()
                .map(SavedTour::getTourId)
                .toList();

        return tourQueryService.findAllByIds(tourIds);
    }

    // ----------------------------------------------------------------
    // HELPERS
    // ----------------------------------------------------------------

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + userId));
    }

    private Role resolveRole(String roleName) {
        return switch (roleName.toUpperCase()) {
            case "ADMINISTRATOR" -> new Administrator();
            case "USER"          -> new UserRole();
            case "VISITOR"       -> new Visitor();
            default -> throw new IllegalArgumentException("Rol no reconocido: " + roleName);
        };
    }

    private UserResponse buildUserResponse(User user, String email) {
        UserProfile profile = user.getUserProfile();
        Role role = resolveRole(profile.getRoleName());
        RoleDTO roleDTO = new RoleDTO(role.getNameRole(), role.getPermissions());

        List<ContactResponse> contactResponses = profile.getContacts().stream()
                .map(c -> new ContactResponse(c.getId(), c.getPhoneNumber()))
                .toList();

        UserProfileResponse profileResponse = new UserProfileResponse(
                profile.getId(),
                profile.getName(),
                profile.getDocument(),
                roleDTO,
                contactResponses
        );

        return new UserResponse(user.getId(), email, profileResponse);
    }
}