package com.proyecto.app.tourManagment.controller;

import com.proyecto.app.media.dto.request.PhotoRequest;
import com.proyecto.app.media.dto.response.AlbumResponse;
import com.proyecto.app.media.dto.response.PhotoResponse;
import com.proyecto.app.media.service.AlbumService;
import com.proyecto.app.tourManagment.domain.Tour;
import com.proyecto.app.tourManagment.exception.TourNotFoundException;
import com.proyecto.app.tourManagment.repository.TourRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tours/{tourId}/media")
public class TourMediaController {

    private final TourRepository tourRepository;
    private final AlbumService albumService;

    public TourMediaController(TourRepository tourRepository, AlbumService albumService) {
        this.tourRepository = tourRepository;
        this.albumService = albumService;
    }

    @GetMapping("/album")
    public ResponseEntity<AlbumResponse> getAlbum(@PathVariable Long tourId) {
        return ResponseEntity.ok(albumService.getAlbumById(resolveAlbumId(tourId)));
    }

    @GetMapping("/photos")
    public ResponseEntity<List<PhotoResponse>> getPhotos(@PathVariable Long tourId) {
        return ResponseEntity.ok(albumService.getPhotosByAlbum(resolveAlbumId(tourId)));
    }

    @GetMapping("/photos/current")
    public ResponseEntity<PhotoResponse> getCurrent(@PathVariable Long tourId) {
        return ResponseEntity.ok(albumService.getCurrentPhotoByAlbum(resolveAlbumId(tourId)));
    }

    @GetMapping("/photos/next")
    public ResponseEntity<PhotoResponse> next(@PathVariable Long tourId) {
        return ResponseEntity.ok(albumService.nextPhotoByAlbum(resolveAlbumId(tourId)));
    }

    @GetMapping("/photos/previous")
    public ResponseEntity<PhotoResponse> previous(@PathVariable Long tourId) {
        return ResponseEntity.ok(albumService.previousPhotoByAlbum(resolveAlbumId(tourId)));
    }

    @PostMapping("/photos")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<AlbumResponse> addPhoto(
            @PathVariable Long tourId,
            @Valid @RequestBody PhotoRequest request) {
        return ResponseEntity.ok(albumService.addPhotoToAlbum(resolveAlbumId(tourId), request));
    }

    @DeleteMapping("/photos/{index}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<AlbumResponse> removePhoto(
            @PathVariable Long tourId,
            @PathVariable int index) {
        return ResponseEntity.ok(albumService.removePhotoByIndexFromAlbum(resolveAlbumId(tourId), index));
    }

    private Long resolveAlbumId(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException(tourId));
        if (tour.getAlbum() == null) {
            throw new TourNotFoundException(tourId);
        }
        return tour.getAlbum().getId();
    }
}
