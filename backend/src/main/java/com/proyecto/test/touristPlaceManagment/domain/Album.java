package com.proyecto.test.touristPlaceManagment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

import com.proyecto.test.utils.PhotoListConverter;

@Getter
@Setter
@Embeddable
public class Album {

    @Convert(converter = PhotoListConverter.class)
    @Column(name = "images")
    private List<Photo> photos = new ArrayList<>();

    private int index;

    public Album() {
    }

    public boolean insertPhoto(Photo photo) {
        return photos.add(photo);
    }

    public boolean deletePhoto(Photo photo) {
        return photos.remove(photo);
    }

    public Photo previousPhoto() {
        if (index > 0)
            index--;
        return photos.get(index);
    }

    public Photo nextPhoto() {
        if (index < photos.size() - 1)
            index++;
        return photos.get(index);
    }

    public Photo getCurrent() {
        return photos.isEmpty() ? null : photos.get(index);
    }
}
