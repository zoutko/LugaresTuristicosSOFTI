package com.proyecto.test.touristPlaceManagment.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.proyecto.test.common.Environment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "places")
public class TouristPlace {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "name")
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
    private com.proyecto.test.common.Location location;

    @Embedded
    private Album album;

    @ManyToMany
    @JoinTable(name = "places_categories", joinColumns = @JoinColumn(name = "place_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<com.proyecto.test.common.Category> categories;

    @OneToMany(mappedBy = "touristPlace", cascade = CascadeType.ALL)
    private List<Activity> activities;

    public TouristPlace() {
    }

    public void addCategory(com.proyecto.test.common.Category category) {
        if (this.categories == null)
            this.categories = new ArrayList<>();
        this.categories.add(category);
    }

    public void removeCategory(com.proyecto.test.common.Category category) {
        if (this.categories != null)
            this.categories.remove(category);
    }

    public void addActivity(Activity activity) {
        if (this.activities == null)
            this.activities = new ArrayList<>();
        activity.setTouristPlace(this);
        this.activities.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (this.activities != null)
            this.activities.remove(activity);
    }

    public String getSummary() {
        return name + " - " + location.getFullLocation();
    }
}
