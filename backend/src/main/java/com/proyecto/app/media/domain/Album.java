package com.proyecto.app.media.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "albums")
@Getter
@Setter
@NoArgsConstructor
public class Album {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "album_id")
    private List<Photo> photos = new ArrayList<>();

    private int currentIndex;

    public Album(String name) {
        this.name = name;
    }


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
