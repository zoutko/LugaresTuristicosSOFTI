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


    public List<Photo> getPhotos(UUID placeId) {
        return touristPlaceService.getById(placeId).getAlbum().getPhotos();
    }


    public TouristPlace addPhoto(UUID placeId, Photo photo) {
        TouristPlace place = touristPlaceService.getById(placeId);
        place.getAlbum().insertPhoto(photo);
        return place;
    }


    public TouristPlace deletePhoto(UUID placeId, Photo photo) {
        TouristPlace place = touristPlaceService.getById(placeId);
        place.getAlbum().deletePhoto(photo);
        return place;
    }


    public Photo getCurrentPhoto(UUID placeId) {
        return touristPlaceService.getById(placeId).getAlbum().getCurrent();
    }


    public Photo nextPhoto(UUID placeId) {
        return touristPlaceService.getById(placeId).getAlbum().nextPhoto();
    }


    public Photo previousPhoto(UUID placeId) {
        return touristPlaceService.getById(placeId).getAlbum().previousPhoto();
    }
}


