package com.proyecto.test.touristPlaceManagment.service;

import org.springframework.stereotype.Service;
import com.proyecto.test.touristPlaceManagment.domain.Photo;
import com.proyecto.test.touristPlaceManagment.domain.TouristPlace;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Service
public class AlbumService {

    @Autowired
    private TouristPlaceService touristPlaceService;

    // Traer todas las fotos de un lugar
    public List<Photo> getPhotos(UUID placeId) {
        return touristPlaceService.getById(placeId).getAlbum().getPhotos();
    }

    // Agregar foto
    public TouristPlace addPhoto(UUID placeId, Photo photo) {
        TouristPlace place = touristPlaceService.getById(placeId);
        place.getAlbum().insertPhoto(photo);
        return place;
    }

    // Eliminar foto
    public TouristPlace deletePhoto(UUID placeId, Photo photo) {
        TouristPlace place = touristPlaceService.getById(placeId);
        place.getAlbum().deletePhoto(photo);
        return place;
    }

    // Foto actual
    public Photo getCurrentPhoto(UUID placeId) {
        return touristPlaceService.getById(placeId).getAlbum().getCurrent();
    }

    // Siguiente foto
    public Photo nextPhoto(UUID placeId) {
        return touristPlaceService.getById(placeId).getAlbum().nextPhoto();
    }

    // Foto anterior
    public Photo previousPhoto(UUID placeId) {
        return touristPlaceService.getById(placeId).getAlbum().previousPhoto();
    }
}


