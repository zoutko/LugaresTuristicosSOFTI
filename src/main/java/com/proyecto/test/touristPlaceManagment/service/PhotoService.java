package com.proyecto.test.touristPlaceManagment.service;

import org.springframework.stereotype.Service;

import com.proyecto.test.touristPlaceManagment.domain.Photo;

import org.springframework.beans.factory.annotation.Autowired;


import java.util.UUID;

@Service
public class PhotoService {

    @Autowired
    private TouristPlaceService touristPlaceService;

    // Validar si una foto es valida
    public boolean isValid(Photo photo) {
        return photo.isValid();
    }

    // Obtener nombre del archivo
    public String getFileName(Photo photo) {
        return photo.getFileName();
    }

    // Buscar foto por id dentro de un lugar
    public Photo getPhotoById(UUID placeId, int photoId) {
        return touristPlaceService.getById(placeId)
            .getAlbum().getPhotos().stream()
            .filter(p -> p.getId() == photoId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Photo not found: " + photoId));
    }

    // Actualizar ruta de una foto
    public Photo updateFilePath(UUID placeId, int photoId, String filePath) {
        Photo photo = getPhotoById(placeId, photoId);
        photo.setFilePath(filePath);
        return photo;
    }

    // Actualizar descripcion de una foto
    public Photo updateDescription(UUID placeId, int photoId, String description) {
        Photo photo = getPhotoById(placeId, photoId);
        photo.setDescription(description);
        return photo;
    }
}


