package com.proyecto.app.media.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;


@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Album {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "album_photos",
            joinColumns = @JoinColumn(name = "place_id")
    )
    private List<Photo> photos = new ArrayList<>();

    private int currentIndex;

    public void insertPhoto(Photo photo) {
        photos.add(photo);
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }

    public Photo previousPhoto() {
        if (currentIndex > 0)
            currentIndex--;
        return photos.get(currentIndex);
    }

    public Photo nextPhoto() {
        if (currentIndex < photos.size() - 1)
            currentIndex++;
        return photos.get(currentIndex);
    }

    public Photo getCurrent() {
        return photos.isEmpty() ? null : photos.get(currentIndex);
    }

    public boolean isEmpty() {
        return photos.isEmpty();
    }
}
