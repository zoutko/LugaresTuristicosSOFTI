package com.proyecto.test.touristPlaceManagment.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.test.touristPlaceManagment.domain.Activity;
import com.proyecto.test.touristPlaceManagment.domain.Photo;
import com.proyecto.test.touristPlaceManagment.domain.TouristPlace;
import com.proyecto.test.touristPlaceManagment.service.ActivityService;
import com.proyecto.test.touristPlaceManagment.service.AlbumService;
import com.proyecto.test.touristPlaceManagment.service.TouristPlaceService;
import com.proyecto.test.common.Environment;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/places")
public class TouristPlaceController {

    @Autowired
    private TouristPlaceService touristPlaceService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ActivityService activityService;

    // ── TouristPlace ──────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<TouristPlace>> getAll() {
        return ResponseEntity.ok(touristPlaceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TouristPlace> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(touristPlaceService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TouristPlace> create(@RequestBody TouristPlace place) {
        return ResponseEntity.ok(touristPlaceService.create(place));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TouristPlace> update(@PathVariable UUID id, @RequestBody TouristPlace place) {
        return ResponseEntity.ok(touristPlaceService.update(id, place));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        touristPlaceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── Búsquedas ─────────────────────────────────────────

    @GetMapping("/search/name/{name}")
    public ResponseEntity<List<TouristPlace>> getByName(@PathVariable String name) {
        return ResponseEntity.ok(touristPlaceService.getByName(name));
    }

    @GetMapping("/search/city/{city}")
    public ResponseEntity<List<TouristPlace>> getByCity(@PathVariable String city) {
        return ResponseEntity.ok(touristPlaceService.getByCity(city));
    }

    @GetMapping("/search/environment/{environment}")
    public ResponseEntity<List<TouristPlace>> getByEnvironment(@PathVariable Environment environment) {
        return ResponseEntity.ok(touristPlaceService.getByEnvironment(environment));
    }

    // ── Activities ────────────────────────────────────────

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<Activity>> getActivities(@PathVariable UUID id) {
        return ResponseEntity.ok(activityService.getActivitiesByPlace(id));
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<TouristPlace> addActivity(@PathVariable UUID id, @RequestBody Activity activity) {
        return ResponseEntity.ok(activityService.addActivity(id, activity));
    }

    @DeleteMapping("/{id}/activities/{activityId}")
    public ResponseEntity<TouristPlace> removeActivity(@PathVariable UUID id, @PathVariable int activityId) {
        Activity activity = activityService.getActivityById(id, activityId);
        return ResponseEntity.ok(touristPlaceService.removeActivity(id, activity));
    }

    @GetMapping("/{id}/photos")
    public ResponseEntity<List<Photo>> getPhotos(@PathVariable UUID id) {
        return ResponseEntity.ok(albumService.getPhotos(id));
    }

    @PostMapping("/{id}/photos")
    public ResponseEntity<TouristPlace> addPhoto(@PathVariable UUID id, @RequestBody Photo photo) {
        return ResponseEntity.ok(albumService.addPhoto(id, photo));
    }

    @GetMapping("/{id}/photos/current")
    public ResponseEntity<Photo> getCurrentPhoto(@PathVariable UUID id) {
        return ResponseEntity.ok(albumService.getCurrentPhoto(id));
    }

    @GetMapping("/{id}/photos/next")
    public ResponseEntity<Photo> nextPhoto(@PathVariable UUID id) {
        return ResponseEntity.ok(albumService.nextPhoto(id));
    }

    @GetMapping("/{id}/photos/previous")
    public ResponseEntity<Photo> previousPhoto(@PathVariable UUID id) {
        return ResponseEntity.ok(albumService.previousPhoto(id));
    }
}
