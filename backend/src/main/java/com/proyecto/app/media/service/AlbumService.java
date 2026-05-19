package com.proyecto.app.media.service;

import com.proyecto.app.media.domain.Album;
import com.proyecto.app.media.domain.Photo;
import com.proyecto.app.media.dto.request.PhotoRequest;
import com.proyecto.app.media.dto.response.AlbumResponse;
import com.proyecto.app.media.dto.response.PhotoResponse;
import com.proyecto.app.media.exception.InvalidAlbumOperationException;
import com.proyecto.app.media.exception.InvalidPhotoException;
import com.proyecto.app.media.exception.MediaNotFoundException;
import com.proyecto.app.media.repository.AlbumRepository;
import com.proyecto.app.touristPlaceManagment.domain.TouristPlace;
import com.proyecto.app.touristPlaceManagment.repository.TouristPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final TouristPlaceRepository placeRepository;
    private final AlbumRepository albumRepository;

    public AlbumService(TouristPlaceRepository placeRepository, AlbumRepository albumRepository) {
        this.placeRepository = placeRepository;
        this.albumRepository = albumRepository;
    }

    // ── Métodos por placeId (mantiene compatibilidad con MediaController) ──

    public AlbumResponse getAlbum(Long placeId) {
        return toAlbumResponse(resolveAlbumByPlace(placeId));
    }

    public List<PhotoResponse> getPhotos(Long placeId) {
        return resolveAlbumByPlace(placeId).getPhotos().stream()
                .map(this::toPhotoResponse).collect(Collectors.toList());
    }

    public PhotoResponse getCurrentPhoto(Long placeId) {
        Album album = resolveAlbumByPlace(placeId);
        requireNonEmpty(album);
        return toPhotoResponse(album.getCurrent());
    }

    public PhotoResponse nextPhoto(Long placeId) {
        Album album = resolveAlbumByPlace(placeId);
        requireNonEmpty(album);
        return toPhotoResponse(album.nextPhoto());
    }

    public PhotoResponse previousPhoto(Long placeId) {
        Album album = resolveAlbumByPlace(placeId);
        requireNonEmpty(album);
        return toPhotoResponse(album.previousPhoto());
    }

    @Transactional
    public AlbumResponse addPhoto(Long placeId, PhotoRequest request) {
        validatePhotoRequest(request);
        TouristPlace place = resolvePlaceOrThrow(placeId);
        place.getAlbum().insertPhoto(toPhoto(request));
        placeRepository.save(place);
        return toAlbumResponse(place.getAlbum());
    }

    @Transactional
    public AlbumResponse removePhotoByIndex(Long placeId, int index) {
        TouristPlace place = resolvePlaceOrThrow(placeId);
        Album album = place.getAlbum();
        validateIndex(album, index);
        album.removePhoto(album.getPhotos().get(index));
        placeRepository.save(place);
        return toAlbumResponse(album);
    }

    // ── Métodos genéricos por Album (reutilizables para Tour u otros) ──

    public AlbumResponse getAlbumById(Long albumId) {
        return toAlbumResponse(resolveAlbumOrThrow(albumId));
    }

    public List<PhotoResponse> getPhotosByAlbum(Long albumId) {
        return resolveAlbumOrThrow(albumId).getPhotos().stream()
                .map(this::toPhotoResponse).collect(Collectors.toList());
    }

    public PhotoResponse getCurrentPhotoByAlbum(Long albumId) {
        Album album = resolveAlbumOrThrow(albumId);
        requireNonEmpty(album);
        return toPhotoResponse(album.getCurrent());
    }

    public PhotoResponse nextPhotoByAlbum(Long albumId) {
        Album album = resolveAlbumOrThrow(albumId);
        requireNonEmpty(album);
        return toPhotoResponse(album.nextPhoto());
    }

    public PhotoResponse previousPhotoByAlbum(Long albumId) {
        Album album = resolveAlbumOrThrow(albumId);
        requireNonEmpty(album);
        return toPhotoResponse(album.previousPhoto());
    }

    @Transactional
    public AlbumResponse addPhotoToAlbum(Long albumId, PhotoRequest request) {
        validatePhotoRequest(request);
        Album album = resolveAlbumOrThrow(albumId);
        album.insertPhoto(toPhoto(request));
        albumRepository.save(album);
        return toAlbumResponse(album);
    }

    @Transactional
    public AlbumResponse removePhotoByIndexFromAlbum(Long albumId, int index) {
        Album album = resolveAlbumOrThrow(albumId);
        validateIndex(album, index);
        album.removePhoto(album.getPhotos().get(index));
        albumRepository.save(album);
        return toAlbumResponse(album);
    }

    // ── Helpers ──

    public Album findOrCreate(String albumName) {
        return albumRepository.findByName(albumName)
                .orElseGet(() -> albumRepository.save(new Album(albumName)));
    }

    private Album resolveAlbumByPlace(Long placeId) {
        return resolvePlaceOrThrow(placeId).getAlbum();
    }

    private TouristPlace resolvePlaceOrThrow(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new MediaNotFoundException("Lugar no encontrado: " + placeId));
    }

    private Album resolveAlbumOrThrow(Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new MediaNotFoundException("Album no encontrado: " + albumId));
    }

    private void requireNonEmpty(Album album) {
        if (album.isEmpty()) throw new InvalidAlbumOperationException("El álbum no contiene fotos");
    }

    private void validatePhotoRequest(PhotoRequest request) {
        if (request == null || request.getFilePath() == null || request.getFilePath().isBlank()) {
            throw new InvalidPhotoException("filePath es obligatorio para una Photo");
        }
    }

    private void validateIndex(Album album, int index) {
        if (index < 0 || index >= album.getPhotos().size()) {
            throw new MediaNotFoundException("Índice de foto fuera de rango: " + index);
        }
    }

    private Photo toPhoto(PhotoRequest req) {
        Photo p = new Photo();
        p.setFilePath(req.getFilePath());
        p.setDescription(req.getDescription());
        return p;
    }

    private PhotoResponse toPhotoResponse(Photo p) {
        return PhotoResponse.builder()
                .filePath(p.getFilePath())
                .fileName(p.getFileName())
                .description(p.getDescription())
                .build();
    }

    private AlbumResponse toAlbumResponse(Album album) {
        return AlbumResponse.builder()
                .currentIndex(album.getCurrentIndex())
                .totalPhotos(album.getPhotos().size())
                .currentPhoto(album.isEmpty() ? null : toPhotoResponse(album.getCurrent()))
                .photos(album.getPhotos().stream().map(this::toPhotoResponse).collect(Collectors.toList()))
                .build();
    }
}