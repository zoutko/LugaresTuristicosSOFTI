package com.proyecto.test.touristPlaceManagment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proyecto.test.utils.Photo;

@Service
public class TouristPlaceService {

    private final Gson gson;

    public TouristPlaceService() {
        this.gson = new Gson();
    }

    public List<Photo> getPhotos(String photosJson) {
        if (photosJson == null || photosJson.isBlank()) {
            return List.of();
        }
        Photo[] photosArray = gson.fromJson(photosJson, Photo[].class);
        if (photosArray == null || photosArray.length == 0) {
            return List.of();
        }
        return List.of(photosArray);

    }

}
