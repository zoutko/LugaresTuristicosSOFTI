package com.proyecto.test.touristPlaceManagment.domain;

import java.util.List;


public class Album {
    
    private int id;
    private List<Photo> photos;
    private int index;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public List<Photo> getPhotos() {
        return photos;
    }
    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    
}
