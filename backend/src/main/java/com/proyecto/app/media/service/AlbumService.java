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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final TouristPlaceRepository placeRepository;
    private final AlbumRepository albumRepository;

    public AlbumService(TouristPlaceRepository placeRepository, AlbumRepository albumRepository) {
        this.placeRepository = placeRepository;
        this.albumRepository = albumRepository;
    }

    public AlbumResponse getAlbum(UUID placeId) {
        Album album = resolveAlbum(placeId);
        return toAlbumResponse(album);
    }

    public List<PhotoResponse> getPhotos(UUID placeId) {
        return resolveAlbum(placeId).getPhotos()
                .stream()
                .map(this::toPhotoResponse)
                .collect(Collectors.toList());
    }

    public PhotoResponse getCurrentPhoto(UUID placeId) {
        Album album = resolveAlbum(placeId);
        requireNonEmpty(album);
        return toPhotoResponse(album.getCurrent());
    }

    public PhotoResponse nextPhoto(UUID placeId) {
        Album album = resolveAlbum(placeId);
        requireNonEmpty(album);
        return toPhotoResponse(album.nextPhoto());
    }

    public PhotoResponse previousPhoto(UUID placeId) {
        Album album = resolveAlbum(placeId);
        requireNonEmpty(album);
        return toPhotoResponse(album.previousPhoto());
    }

    @Transactional
    public AlbumResponse addPhoto(UUID placeId, PhotoRequest request) {
        validatePhotoRequest(request);
        TouristPlace place = resolvePlaceOrThrow(placeId);
        place.getAlbum().insertPhoto(toPhoto(request));
        placeRepository.save(place);
        return toAlbumResponse(place.getAlbum());
    }

    @Transactional
    public AlbumResponse removePhotoByIndex(UUID placeId, int index) {
        TouristPlace place = resolvePlaceOrThrow(placeId);
        Album album = place.getAlbum();
        List<Photo> photos = album.getPhotos();

        if (index < 0 || index >= photos.size()) {
            throw new MediaNotFoundException("Índice de foto fuera de rango: " + index);
        }

        album.removePhoto(photos.get(index));
        placeRepository.save(place);
        return toAlbumResponse(album);
    }

    private Album resolveAlbum(UUID placeId) {
        return resolvePlaceOrThrow(placeId).getAlbum();
    }

    private TouristPlace resolvePlaceOrThrow(UUID placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new MediaNotFoundException("Lugar no encontrado: " + placeId));
    }

    private void requireNonEmpty(Album album) {
        if (album.isEmpty()) {
            throw new InvalidAlbumOperationException("El álbum no contiene fotos");
        }
    }

    private void validatePhotoRequest(PhotoRequest request) {
        if (request == null || request.getFilePath() == null || request.getFilePath().isBlank()) {
            throw new InvalidPhotoException("filePath es obligatorio para una Photo");
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

    public Album findOrCreate(String albumName) {
        return albumRepository.findByName(albumName)
                .orElseGet(() -> albumRepository.save(new Album(albumName)));
    }
}
