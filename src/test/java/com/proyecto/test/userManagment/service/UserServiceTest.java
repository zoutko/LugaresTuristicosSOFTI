package com.proyecto.test.userManagment.service;

import com.proyecto.test.common.User;
import com.proyecto.test.security.domain.Credential;
import com.proyecto.test.security.service.CredentialService;
import com.proyecto.test.userManagment.domain.Contact;
import com.proyecto.test.userManagment.domain.UserProfile;
import com.proyecto.test.userManagment.dto.request.CreateUserRequest;
import com.proyecto.test.userManagment.dto.request.UpdateContactRequest;
import com.proyecto.test.userManagment.dto.request.UpdateUserRequest;
import com.proyecto.test.userManagment.dto.response.UserResponse;
import com.proyecto.test.userManagment.repository.ContactRepository;
import com.proyecto.test.userManagment.repository.UserProfileRepository;
import com.proyecto.test.userManagment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private CredentialService credentialService;

    @InjectMocks
    private UserService userService;

    private UserProfile profile;
    private User user;
    private Credential credential;

@BeforeEach
    void setUp() {
        profile = new UserProfile("Juan Perez", "123456789", "USER");
        setProfileId(profile, 1L);

        Contact contact = new Contact("3001234567", profile);
        profile.addContact(contact);

        user = new User(profile);  // ← sin credentialId
        setUserId(user, 1L);

        credential = new Credential("juan@mail.com", "encodedPassword", "USER", 1L);
    }
    
    private CreateUserRequest buildCreateRequest(String roleName) {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Juan Perez");
        request.setDocument("123456789");
        request.setRoleName(roleName);
        request.setPhoneNumbers(List.of("3001234567"));
        request.setEmail("juan@mail.com");
        request.setPassword("password123");
        return request;
    }

    // ================================================================
    // CREATE USER
    // ================================================================

    @Nested
    @DisplayName("createUser()")
    class CreateUserTests {

        @Test
        @DisplayName("Debe crear usuario correctamente con rol USER")
        void shouldCreateUserSuccessfully() {
            CreateUserRequest request = buildCreateRequest("USER");

            when(userProfileRepository.existsByDocument("123456789")).thenReturn(false);
            when(userProfileRepository.save(any())).thenReturn(profile);
            when(userRepository.save(any())).thenReturn(user);
            when(credentialService.createCredential(any())).thenReturn(credential);

            UserResponse response = userService.createUser(request);

            assertNotNull(response);
            assertEquals("juan@mail.com", response.getEmail());
            assertEquals("Juan Perez", response.getProfile().getName());
            assertEquals("USER", response.getProfile().getRole().getName());
            verify(userRepository).save(any());
            verify(credentialService).createCredential(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si se intenta crear un ADMINISTRATOR")
        void shouldThrowWhenCreatingAdmin() {
            CreateUserRequest request = buildCreateRequest("ADMINISTRATOR");

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.createUser(request)
            );

            assertEquals("No se puede registrar un administrador desde este endpoint.", ex.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el documento ya existe")
        void shouldThrowWhenDocumentAlreadyExists() {
            CreateUserRequest request = buildCreateRequest("USER");
            when(userProfileRepository.existsByDocument("123456789")).thenReturn(true);

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.createUser(request)
            );

            assertEquals("Ya existe un usuario con ese documento.", ex.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si no se envían teléfonos de contacto")
        void shouldThrowWhenNoPhoneNumbers() {
            CreateUserRequest request = buildCreateRequest("USER");
            request.setPhoneNumbers(List.of());

            when(userProfileRepository.existsByDocument(any())).thenReturn(false);

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.createUser(request)
            );

            assertEquals("Se requiere al menos un número de contacto.", ex.getMessage());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el rol no existe")
        void shouldThrowWhenRoleIsInvalid() {
            CreateUserRequest request = buildCreateRequest("SUPERADMIN");
            when(userProfileRepository.existsByDocument(any())).thenReturn(false);

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.createUser(request)
            );

            assertTrue(ex.getMessage().contains("Rol no reconocido"));
        }
    }

    // ================================================================
    // GET PROFILE
    // ================================================================

    @Nested
    @DisplayName("getProfile()")
    class GetProfileTests {

        @Test
        @DisplayName("Debe retornar el perfil del usuario correctamente")
        void shouldReturnUserProfile() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(credentialService.getCredentialByUserId(any())).thenReturn(credential);

            UserResponse response = userService.getProfile(1L);

            assertNotNull(response);
            assertEquals("Juan Perez", response.getProfile().getName());
            assertEquals("juan@mail.com", response.getEmail());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el usuario no existe")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.getProfile(99L)
            );

            assertTrue(ex.getMessage().contains("Usuario no encontrado"));
        }
    }

    // ================================================================
    // UPDATE INFORMATION
    // ================================================================

    @Nested
    @DisplayName("updateInformation()")
    class UpdateInformationTests {

        @Test
        @DisplayName("Debe actualizar el nombre correctamente")
        void shouldUpdateNameSuccessfully() {
            UpdateUserRequest request = new UpdateUserRequest("name", "Carlos Lopez");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userProfileRepository.save(any())).thenReturn(profile);
            when(credentialService.getCredentialByUserId(any())).thenReturn(credential);

            UserResponse response = userService.updateInformation(1L, request);

            assertEquals("Carlos Lopez", response.getProfile().getName());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el campo no es reconocido")
        void shouldThrowWhenFieldIsInvalid() {
            UpdateUserRequest request = new UpdateUserRequest("email", "nuevo@mail.com");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.updateInformation(1L, request)
            );
        }

        @Test
        @DisplayName("Debe lanzar excepción si el usuario no existe")
        void shouldThrowWhenUserNotFound() {
            UpdateUserRequest request = new UpdateUserRequest("name", "Carlos");
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.updateInformation(99L, request)
            );
        }
    }

    // ================================================================
    // DELETE ACCOUNT
    // ================================================================

    @Nested
    @DisplayName("deleteAccount()")
    class DeleteAccountTests {

        @Test
        @DisplayName("Debe eliminar la cuenta correctamente")
        void shouldDeleteAccountSuccessfully() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            assertDoesNotThrow(() -> userService.deleteAccount(1L));
            verify(userRepository).delete(user);
        }

        @Test
        @DisplayName("Debe lanzar excepción si el usuario no existe")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.deleteAccount(99L)
            );

            verify(userRepository, never()).delete(any());
        }
    }

    // ================================================================
    // CONTACTS
    // ================================================================

    @Nested
    @DisplayName("addContact()")
    class AddContactTests {

        @Test
        @DisplayName("Debe agregar un contacto correctamente")
        void shouldAddContactSuccessfully() {
            UpdateContactRequest request = new UpdateContactRequest(null, "3109876543");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userProfileRepository.save(any())).thenReturn(profile);
            when(credentialService.getCredentialByUserId(any())).thenReturn(credential);

            UserResponse response = userService.addContact(1L, request);

            assertNotNull(response);
            verify(userProfileRepository).save(any());
        }
    }

    @Nested
    @DisplayName("updateContact()")
    class UpdateContactTests {

        @Test
        @DisplayName("Debe actualizar un contacto correctamente")
        void shouldUpdateContactSuccessfully() {
            Contact contact = new Contact("3001234567", profile);
            setContactId(contact, 10L);

            UpdateContactRequest request = new UpdateContactRequest(10L, "3119999999");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));
            when(credentialService.getCredentialByUserId(any())).thenReturn(credential);

            UserResponse response = userService.updateContact(1L, request);

            assertNotNull(response);
            assertEquals("3119999999", contact.getPhoneNumber());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el contacto no pertenece al usuario")
        void shouldThrowWhenContactDoesNotBelongToUser() {
            UserProfile otherProfile = new UserProfile("Otro", "999", "USER");
            setProfileId(otherProfile, 99L);
            Contact contact = new Contact("3001234567", otherProfile);
            setContactId(contact, 10L);

            UpdateContactRequest request = new UpdateContactRequest(10L, "3119999999");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));

            assertThrows(
                    SecurityException.class,
                    () -> userService.updateContact(1L, request)
            );
        }
    }

    @Nested
    @DisplayName("deleteContact()")
    class DeleteContactTests {

        @Test
        @DisplayName("Debe lanzar excepción si solo hay un contacto")
        void shouldThrowWhenOnlyOneContact() {
            Contact contact = new Contact("3001234567", profile);
            setContactId(contact, 10L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));
            when(contactRepository.findAllByUserProfileId(any())).thenReturn(List.of(contact));

            assertThrows(
                    IllegalStateException.class,
                    () -> userService.deleteContact(1L, 10L)
            );
        }

        @Test
        @DisplayName("Debe eliminar el contacto correctamente si hay más de uno")
        void shouldDeleteContactSuccessfully() {
            Contact contact1 = new Contact("3001234567", profile);
            Contact contact2 = new Contact("3109999999", profile);
            setContactId(contact1, 10L);
            setContactId(contact2, 11L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(contactRepository.findById(10L)).thenReturn(Optional.of(contact1));
            when(contactRepository.findAllByUserProfileId(any())).thenReturn(List.of(contact1, contact2));

            assertDoesNotThrow(() -> userService.deleteContact(1L, 10L));
            verify(contactRepository).delete(contact1);
        }
    }


    private void setContactId(Contact contact, Long id) {
        try {
            var field = Contact.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(contact, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setProfileId(UserProfile profile, Long id) {
        try {
            var field = UserProfile.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(profile, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setUserId(User user, Long id) {
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}