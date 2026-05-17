package com.proyecto.test.userManagment;

import com.proyecto.app.common.User;
import com.proyecto.app.security.domain.Credential;
import com.proyecto.app.security.dto.request.RegisterRequest;
import com.proyecto.app.security.service.CredentialService;
import com.proyecto.app.userManagment.domain.UserProfile;
import com.proyecto.app.userManagment.dto.request.CreateUserRequest;
import com.proyecto.app.userManagment.dto.request.UpdateUserRequest;
import com.proyecto.app.userManagment.dto.response.UserResponse;
import com.proyecto.app.userManagment.repository.ContactRepository;
import com.proyecto.app.userManagment.repository.UserProfileRepository;
import com.proyecto.app.userManagment.repository.UserRepository;
import com.proyecto.app.userManagment.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Pruebas Unitarias")
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

    private UserProfile userProfile;
    private User user;
    private Credential credential;

    @BeforeEach
    void setUp() {
        userProfile = mock(UserProfile.class);
        user = mock(User.class);
        credential = mock(Credential.class);
    }

    @Test
    @DisplayName("createUser: lanza excepción si el rol es ADMINISTRATOR")
    void createUser_throwsIfRoleIsAdministrator() {
        CreateUserRequest request = new CreateUserRequest();
        request.setRoleName("ADMINISTRATOR");

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No se puede registrar un administrador");
    }

    @Test
    @DisplayName("createUser: lanza excepción si el documento ya existe")
    void createUser_throwsIfDocumentAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setRoleName("USER");
        request.setDocument("123456789");
        request.setPhoneNumbers(List.of("3001234567"));

        when(userProfileRepository.existsByDocument("123456789")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe un usuario con ese documento");
    }

    @Test
    @DisplayName("createUser: lanza excepción si no hay números de contacto")
    void createUser_throwsIfNoPhoneNumbers() {
        CreateUserRequest request = new CreateUserRequest();
        request.setRoleName("USER");
        request.setDocument("999");
        request.setPhoneNumbers(List.of());

        when(userProfileRepository.existsByDocument("999")).thenReturn(false);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("al menos un número de contacto");
    }

    @Test
    @DisplayName("createUser: lanza excepción si el rol es desconocido")
    void createUser_throwsIfRoleIsUnknown() {
        CreateUserRequest request = new CreateUserRequest();
        request.setRoleName("SUPERADMIN");
        request.setDocument("000");
        request.setPhoneNumbers(List.of("3001234567"));

        when(userProfileRepository.existsByDocument("000")).thenReturn(false);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rol no reconocido");
    }

    @Test
    @DisplayName("createUser: crea usuario VISITOR exitosamente")
    void createUser_successForVisitor() {
        CreateUserRequest request = new CreateUserRequest();
        request.setRoleName("VISITOR");
        request.setDocument("987654321");
        request.setName("Maria Lopez");
        request.setEmail("maria@example.com");
        request.setPassword("pass123");
        request.setPhoneNumbers(List.of("3109876543"));

        when(userProfileRepository.existsByDocument("987654321")).thenReturn(false);

        UserProfile savedProfile = mock(UserProfile.class);

        User savedUser = mock(User.class);
        when(savedUser.getUserProfile()).thenReturn(savedProfile);

        when(userProfileRepository.save(any())).thenReturn(savedProfile);
        when(userRepository.save(any())).thenReturn(savedUser);
        when(credentialService.createCredential(any(RegisterRequest.class))).thenReturn(credential);
        when(credential.getEmail()).thenReturn("maria@example.com");

        UserResponse response = userService.createUser(request);

        assertThat(response).isNotNull();

        verify(userProfileRepository).save(any(UserProfile.class));
        verify(userRepository).save(any(User.class));
        verify(credentialService).createCredential(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("getProfile: lanza excepción si el usuario no existe")
    void getProfile_throwsIfUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfile(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    @DisplayName("getProfile: retorna respuesta correcta si el usuario existe")
    void getProfile_returnsResponseIfUserFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(user.getUserProfile()).thenReturn(userProfile);

        when(userProfile.getId()).thenReturn(10L);
        when(userProfile.getName()).thenReturn("Juan Perez");
        when(userProfile.getDocument()).thenReturn("123456789");
        when(userProfile.getRoleName()).thenReturn("USER");
        when(userProfile.getContacts()).thenReturn(List.of());

        when(credentialService.getCredentialByUserId(1L))
                .thenReturn(credential);

        when(credential.getEmail())
                .thenReturn("juan@example.com");

        UserResponse response = userService.getProfile(1L);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("juan@example.com");
    }

    @Test
    @DisplayName("updateInformation: actualiza y retorna respuesta")
    void updateInformation_updatesAndReturns() {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setField("name");
        request.setNewValue("Juan Actualizado");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(user.getUserProfile()).thenReturn(userProfile);

        when(userProfile.getId()).thenReturn(10L);
        when(userProfile.getName()).thenReturn("Juan Actualizado");
        when(userProfile.getDocument()).thenReturn("123456789");
        when(userProfile.getRoleName()).thenReturn("USER");
        when(userProfile.getContacts()).thenReturn(List.of());

        when(credentialService.getCredentialByUserId(1L))
                .thenReturn(credential);

        when(credential.getEmail())
                .thenReturn("juan@example.com");

        UserResponse response = userService.updateInformation(1L, request);

        assertThat(response).isNotNull();

        verify(userProfile).updateInformation("name", "Juan Actualizado");
        verify(userProfileRepository).save(userProfile);
    }

    @Test
    @DisplayName("deleteAccount: elimina usuario correctamente")
    void deleteAccount_deletesUser() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteAccount(1L);

        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("deleteAccount: lanza excepción si el usuario no existe")
    void deleteAccount_throwsIfNotFound() {

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteAccount(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuario no encontrado");
    }
}