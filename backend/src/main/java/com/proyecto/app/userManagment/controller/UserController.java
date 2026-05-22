package com.proyecto.app.userManagment.controller;

import com.proyecto.app.tourManagment.dto.response.TourResponse;
import com.proyecto.app.userManagment.dto.request.CreateUserRequest;
import com.proyecto.app.userManagment.dto.request.UpdateContactRequest;
import com.proyecto.app.userManagment.dto.request.UpdateUserRequest;
import com.proyecto.app.userManagment.dto.response.UserResponse;
import com.proyecto.app.userManagment.service.UserService;

import java.util.List;

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


    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or #userId == authentication.principal.userId")
    public ResponseEntity<UserResponse> getProfile(@PathVariable Long userId) {
        UserResponse response = userService.getProfile(userId);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or #userId == authentication.principal.userId")
    public ResponseEntity<UserResponse> updateInformation(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateInformation(userId, request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('USER') and #userId == authentication.principal.userId")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long userId) {
        userService.deleteAccount(userId);
        return ResponseEntity.noContent().build();
    }


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


    @PostMapping("/{userId}/saved-tours/{tourId}")
    @PreAuthorize("hasRole('USER') and #userId == authentication.principal.userId")
    public ResponseEntity<Void> saveTour(
            @PathVariable Long userId,
            @PathVariable Long tourId) {
        userService.saveTour(userId, tourId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}/saved-tours/{tourId}")
    @PreAuthorize("hasRole('USER') and #userId == authentication.principal.userId")
    public ResponseEntity<Void> removeSavedTour(
            @PathVariable Long userId,
            @PathVariable Long tourId) {
        userService.removeSavedTour(userId, tourId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/saved-tours")
    @PreAuthorize("hasRole('USER') and #userId == authentication.principal.userId")
    public ResponseEntity<List<TourResponse>> getSavedTours(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getSavedTours(userId));
    }
}