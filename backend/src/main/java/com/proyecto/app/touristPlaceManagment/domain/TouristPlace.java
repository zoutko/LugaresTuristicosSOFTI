package com.proyecto.app.touristPlaceManagment.domain;

import com.proyecto.app.common.Category;
import com.proyecto.app.common.Environment;
import com.proyecto.app.common.Location;
import com.proyecto.app.media.domain.Album;   // ← única dependencia de media

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "places")
public class TouristPlace {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "cancelation_policy")
    private String cancelationPolicy;

    @Column(name = "duration")
    private String duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "environment")
    private Environment environment;

    @Embedded
    private Location location;


    @ManyToOne(cascade = CascadeType.PERSIST) //Implementar en tour
    @JoinColumn(name = "album_id")
    private Album album;

    @ManyToMany
    @JoinTable(
        name = "places_categories",
        joinColumns = @JoinColumn(name = "place_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "touristPlace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Activity> activities = new ArrayList<>();


    public void addActivity(Activity activity) {
        activity.setTouristPlace(this);
        activities.add(activity);
    }

    public void removeActivity(Activity activity) {
        activities.remove(activity);
        activity.setTouristPlace(null);
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
    }

    public String getSummary() {
        return name + " - " + location.getFullLocation();
    }
}
