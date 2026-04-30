package com.proyecto.test.touristPlaceManagment.domain;

import com.proyecto.test.utils.Album;
import com.proyecto.test.utils.Category;
import com.proyecto.test.utils.Enviroment;
import com.proyecto.test.utils.Location;

public class TouristPlace {
    
    private Long id;
    private String name;
    private Location location;
    private Category category;
    private Enviroment enviroment;
    private String activities;
    private String cancelationPolicy;
    private Times duration;
    private Album album;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
    public Enviroment getEnviroment() {
        return enviroment;
    }
    public void setEnviroment(Enviroment enviroment) {
        this.enviroment = enviroment;
    }
    public String getActivities() {
        return activities;
    }
    public void setActivities(String activities) {
        this.activities = activities;
    }
    public String getCancelationPolicy() {
        return cancelationPolicy;
    }
    public void setCancelationPolicy(String cancelationPolicy) {
        this.cancelationPolicy = cancelationPolicy;
    }
    public Times getDuration() {
        return duration;
    }
    public void setDuration(Times duration) {
        this.duration = duration;
    }
    public Album getAlbum() {
        return album;
    }
    public void setAlbum(Album album) {
        this.album = album;
    }
}
