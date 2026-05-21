package com.proyecto.app.media.controller;

import com.proyecto.app.media.dto.request.PhotoRequest;
import com.proyecto.app.media.dto.response.AlbumResponse;
import com.proyecto.app.media.dto.response.PhotoResponse;
import com.proyecto.app.media.service.AlbumService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/places/{placeId}/media")
public class MediaController {

    private final AlbumService albumService;

    public MediaController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("/album")
    public ResponseEntity<AlbumResponse> getAlbum(@PathVariable Long placeId) {
        return ResponseEntity.ok(albumService.getAlbum(placeId));
    }

    @GetMapping("/photos")
    public ResponseEntity<List<PhotoResponse>> getPhotos(@PathVariable Long placeId) {
        return ResponseEntity.ok(albumService.getPhotos(placeId));
    }

    @PostMapping("/photos")
    public ResponseEntity<AlbumResponse> addPhoto(
            @PathVariable Long placeId,
            @Valid @RequestBody PhotoRequest request) {
        return ResponseEntity.ok(albumService.addPhoto(placeId, request));
    }

    @DeleteMapping("/photos/{index}")
    public ResponseEntity<AlbumResponse> removePhoto(
            @PathVariable Long placeId,
            @PathVariable int index) {
        return ResponseEntity.ok(albumService.removePhotoByIndex(placeId, index));
    }

    @GetMapping("/photos/current")
    public ResponseEntity<PhotoResponse> getCurrent(@PathVariable Long placeId) {
        return ResponseEntity.ok(albumService.getCurrentPhoto(placeId));
    }

    @GetMapping("/photos/next")
    public ResponseEntity<PhotoResponse> next(@PathVariable Long placeId) {
        return ResponseEntity.ok(albumService.nextPhoto(placeId));
    }

    @GetMapping("/photos/previous")
    public ResponseEntity<PhotoResponse> previous(@PathVariable Long placeId) {
        return ResponseEntity.ok(albumService.previousPhoto(placeId));
    }
}
