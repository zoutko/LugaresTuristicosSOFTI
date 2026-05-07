package com.proyecto.test.touristPlaceManagment.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.proyecto.test.touristPlaceManagment.domain.Photo;

import java.util.UUID;
import java.util.List;

@Service
public class PhotoService {

    @Autowired
    private TouristPlaceService touristPlaceService;

    
    public boolean isValid(Photo photo) {
        return photo != null && photo.isValid();
    }

    public String getFileName(Photo photo) {
        return photo != null ? photo.getFileName() : "";
    }

    public List<Photo> getPhotos(UUID placeId) {
        return touristPlaceService.getById(placeId)
                .getAlbum()
                .getPhotos();
    }

    public Photo getPhotoByIndex(UUID placeId, int index) {
        List<Photo> photos = getPhotos(placeId);

        if (index < 0 || index >= photos.size()) {
            throw new RuntimeException("Photo index out of bounds: " + index);
        }

        return photos.get(index);
    }


    public Photo updateFilePath(UUID placeId, int index, String filePath) {
        Photo photo = getPhotoByIndex(placeId, index);
        photo.setFilePath(filePath);
        return photo;
    }


    public Photo updateDescription(UUID placeId, int index, String description) {
        Photo photo = getPhotoByIndex(placeId, index);
        photo.setDescription(description);
        return photo;
    }


    public Photo addPhoto(UUID placeId, Photo photo) {
        if (!isValid(photo)) {
            throw new RuntimeException("Invalid photo");
        }

        List<Photo> photos = getPhotos(placeId);
        photos.add(photo);

        return photo;
    }

    public void deletePhoto(UUID placeId, int index) {
        List<Photo> photos = getPhotos(placeId);

        if (index < 0 || index >= photos.size()) {
            throw new RuntimeException("Photo index out of bounds: " + index);
        }

        photos.remove(index);
    }
}