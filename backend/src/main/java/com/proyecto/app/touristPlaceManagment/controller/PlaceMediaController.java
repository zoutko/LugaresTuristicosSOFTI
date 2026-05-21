package com.proyecto.app.touristPlaceManagment.controller;

import com.proyecto.app.media.domain.Album;
import com.proyecto.app.media.dto.request.PhotoRequest;
import com.proyecto.app.media.dto.response.AlbumResponse;
import com.proyecto.app.media.dto.response.PhotoResponse;
import com.proyecto.app.media.service.AlbumService;
import com.proyecto.app.touristPlaceManagment.domain.TouristPlace;
import com.proyecto.app.touristPlaceManagment.exception.TouristPlaceNotFoundException;
import com.proyecto.app.touristPlaceManagment.repository.TouristPlaceRepository;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places/{placeId}/media")
public class PlaceMediaController {

    private final TouristPlaceRepository placeRepository;
    private final AlbumService albumService;

    public PlaceMediaController(TouristPlaceRepository placeRepository, AlbumService albumService) {
        this.placeRepository = placeRepository;
        this.albumService = albumService;
    }

    @GetMapping("/album")
    public ResponseEntity<AlbumResponse> getAlbum(@PathVariable Long placeId) {
        return ResponseEntity.ok(albumService.getAlbumById(resolveAlbumId(placeId)));
    }

    @GetMapping("/photos")
    public ResponseEntity<List<PhotoResponse>> getPhotos(@PathVariable Long placeId) {
        return ResponseEntity.ok(albumService.getPhotosByAlbum(resolveAlbumId(placeId)));
    }

    @GetMapping("/photos/current")
    public ResponseEntity<PhotoResponse> getCurrent(@PathVariable Long placeId) {
        return ResponseEntity.ok(albumService.getCurrentPhotoByAlbum(resolveAlbumId(placeId)));
    }

    @GetMapping("/photos/next")
    public ResponseEntity<PhotoResponse> next(@PathVariable Long placeId) {
        return ResponseEntity.ok(albumService.nextPhotoByAlbum(resolveAlbumId(placeId)));
    }

    @GetMapping("/photos/previous")
    public ResponseEntity<PhotoResponse> previous(@PathVariable Long placeId) {
        return ResponseEntity.ok(albumService.previousPhotoByAlbum(resolveAlbumId(placeId)));
    }

    @PostMapping("/photos")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<AlbumResponse> addPhoto(
            @PathVariable Long placeId,
            @Valid @RequestBody PhotoRequest request) {
        return ResponseEntity.ok(albumService.addPhotoToAlbum(resolveAlbumId(placeId), request));
    }

    @DeleteMapping("/photos/{index}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<AlbumResponse> removePhoto(
            @PathVariable Long placeId,
            @PathVariable int index) {
        return ResponseEntity.ok(albumService.removePhotoByIndexFromAlbum(resolveAlbumId(placeId), index));
    }

    private Long resolveAlbumId(Long placeId) {
        TouristPlace place = placeRepository.findById(placeId)
                .orElseThrow(() -> new TouristPlaceNotFoundException(placeId.toString()));
        if (place.getAlbum() == null) {
            Album album = new Album(place.getName());
            place.setAlbum(album);
            placeRepository.save(place);
        }
        return place.getAlbum().getId();
    }
}