package com.proyecto.test.touristPlaceManagment.controller;

import org.springframework.stereotype.Controller;

import com.proyecto.test.touristPlaceManagment.service.AlbumService;
import com.proyecto.test.touristPlaceManagment.service.PhotoService;
import com.proyecto.test.touristPlaceManagment.service.TimesService;
import com.proyecto.test.touristPlaceManagment.service.TouristPlaceService;

@Controller
public class TouristPlaceController {
    
    private final TouristPlaceService touristPlaceService;
    private final PhotoService photoService;
    private final AlbumService albumService;
    private final TimesService timesService;

    public TouristPlaceController(TouristPlaceService touristPlaceService, PhotoService photoService, AlbumService albumService, TimesService timesService) {
        this.touristPlaceService = touristPlaceService;
        this.photoService = photoService;
        this.albumService = albumService;
        this.timesService = timesService;
    }
 
}
