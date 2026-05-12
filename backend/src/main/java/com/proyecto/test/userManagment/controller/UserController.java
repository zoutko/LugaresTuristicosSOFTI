package com.proyecto.test.userManagment.controller;

import com.proyecto.test.userManagment.dto.request.CreateUserRequest;
import com.proyecto.test.userManagment.dto.request.UpdateContactRequest;
import com.proyecto.test.userManagment.dto.request.UpdateUserRequest;
import com.proyecto.test.userManagment.dto.response.UserResponse;
import com.proyecto.test.userManagment.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ----------------------------------------------------------------
    // CREATE — cualquiera puede crear cuenta (VISITOR)
    // ----------------------------------------------------------------

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ----------------------------------------------------------------
    // READ — el propio usuario o un administrador pueden ver el perfil
    // ----------------------------------------------------------------

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or #userId == authentication.principal.userId")
    public ResponseEntity<UserResponse> getProfile(@PathVariable Long userId) {
        UserResponse response = userService.getProfile(userId);
        return ResponseEntity.ok(response);
    }

    // ----------------------------------------------------------------
    // UPDATE perfil — el propio usuario o administrador
    // ----------------------------------------------------------------

    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or #userId == authentication.principal.userId")
    public ResponseEntity<UserResponse> updateInformation(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateInformation(userId, request);
        return ResponseEntity.ok(response);
    }

    // ----------------------------------------------------------------
    // DELETE cuenta — solo el propio usuario (USER)
    // ----------------------------------------------------------------

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('USER') and #userId.equals(authentication.principal.userId)")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long userId) {
        userService.deleteAccount(userId);
        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------------------
    // CONTACTS
    // ----------------------------------------------------------------

    @PostMapping("/{userId}/contacts")
    @PreAuthorize("hasRole('ADMINISTRATOR') or #userId == authentication.principal.userId")
    public ResponseEntity<UserResponse> addContact(
            @PathVariable Long userId,
            @RequestBody UpdateContactRequest request) {
        UserResponse response = userService.addContact(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{userId}/contacts")
    @PreAuthorize("hasRole('ADMINISTRATOR') or #userId == authentication.principal.userId")
    public ResponseEntity<UserResponse> updateContact(
            @PathVariable Long userId,
            @RequestBody UpdateContactRequest request) {
        UserResponse response = userService.updateContact(userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/contacts/{contactId}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or #userId == authentication.principal.userId")
    public ResponseEntity<Void> deleteContact(
            @PathVariable Long userId,
            @PathVariable Long contactId) {
        userService.deleteContact(userId, contactId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/role")
    public ResponseEntity<?> myRole(org.springframework.security.core.Authentication auth) {
        return ResponseEntity.ok(auth.getAuthorities());
    }
}