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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public Album findOrCreate(String name) {
        return albumRepository.findByName(name)
                .orElseGet(() -> albumRepository.save(new Album(name)));
    }

    public AlbumResponse getAlbumById(Long albumId) {
        return toAlbumResponse(resolveOrThrow(albumId));
    }

    public List<PhotoResponse> getPhotosByAlbum(Long albumId) {
        return resolveOrThrow(albumId).getPhotos().stream()
                .map(this::toPhotoResponse)
                .collect(Collectors.toList());
    }

    public PhotoResponse getCurrentPhotoByAlbum(Long albumId) {
        Album album = resolveOrThrow(albumId);
        requireNonEmpty(album);
        return toPhotoResponse(album.getCurrent());
    }

    public PhotoResponse nextPhotoByAlbum(Long albumId) {
        Album album = resolveOrThrow(albumId);
        requireNonEmpty(album);
        return toPhotoResponse(album.nextPhoto());
    }

    public PhotoResponse previousPhotoByAlbum(Long albumId) {
        Album album = resolveOrThrow(albumId);
        requireNonEmpty(album);
        return toPhotoResponse(album.previousPhoto());
    }

    @Transactional
    public AlbumResponse addPhotoToAlbum(Long albumId, PhotoRequest request) {
        validatePhotoRequest(request);
        Album album = resolveOrThrow(albumId);
        album.insertPhoto(toPhoto(request));
        return toAlbumResponse(albumRepository.save(album));
    }

    @Transactional
    public AlbumResponse removePhotoByIndexFromAlbum(Long albumId, int index) {
        Album album = resolveOrThrow(albumId);
        List<Photo> photos = album.getPhotos();
        if (index < 0 || index >= photos.size()) {
            throw new MediaNotFoundException("Índice de foto fuera de rango: " + index);
        }
        album.removePhoto(photos.get(index));
        return toAlbumResponse(albumRepository.save(album));
    }

    private Album resolveOrThrow(Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new MediaNotFoundException("Álbum no encontrado: " + albumId));
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
}