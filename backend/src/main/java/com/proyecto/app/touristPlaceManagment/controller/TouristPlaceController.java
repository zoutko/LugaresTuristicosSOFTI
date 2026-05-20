package com.proyecto.app.touristPlaceManagment.controller;

import com.proyecto.app.common.Environment;
import com.proyecto.app.touristPlaceManagment.dto.request.ActivityRequest;
import com.proyecto.app.touristPlaceManagment.dto.request.TouristPlaceRequest;
import com.proyecto.app.touristPlaceManagment.dto.response.ActivityResponse;
import com.proyecto.app.touristPlaceManagment.dto.response.TouristPlaceResponse;
import com.proyecto.app.touristPlaceManagment.service.ActivityService;
import com.proyecto.app.touristPlaceManagment.service.TouristPlaceService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/places")
public class TouristPlaceController {

    private final TouristPlaceService touristPlaceService;
    private final ActivityService activityService;

    public TouristPlaceController(TouristPlaceService touristPlaceService,
                                   ActivityService activityService) {
        this.touristPlaceService = touristPlaceService;
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<List<TouristPlaceResponse>> getAll() {
        return ResponseEntity.ok(touristPlaceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TouristPlaceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(touristPlaceService.getById(id));
    }

    @GetMapping("/search/name/{name}")
    public ResponseEntity<List<TouristPlaceResponse>> getByName(@PathVariable String name) {
        return ResponseEntity.ok(touristPlaceService.getByName(name));
    }

    @GetMapping("/search/city/{city}")
    public ResponseEntity<List<TouristPlaceResponse>> getByCity(@PathVariable String city) {
        return ResponseEntity.ok(touristPlaceService.getByCity(city));
    }

    @GetMapping("/search/environment/{environment}")
    public ResponseEntity<List<TouristPlaceResponse>> getByEnvironment(
            @PathVariable Environment environment) {
        return ResponseEntity.ok(touristPlaceService.getByEnvironment(environment));
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityResponse>> getActivities(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getActivitiesByPlace(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<TouristPlaceResponse> create(@Valid @RequestBody TouristPlaceRequest request) {
        return ResponseEntity.ok(touristPlaceService.create(request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<TouristPlaceResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TouristPlaceRequest request) {
        return ResponseEntity.ok(touristPlaceService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        touristPlaceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activities")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<TouristPlaceResponse> addActivity(
            @PathVariable Long id,
            @Valid @RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityService.addActivity(id, request));
    }

    @DeleteMapping("/{id}/activities/{activityId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<TouristPlaceResponse> removeActivity(
            @PathVariable Long id,
            @PathVariable int activityId) {
        return ResponseEntity.ok(activityService.removeActivity(id, activityId));
    }
}