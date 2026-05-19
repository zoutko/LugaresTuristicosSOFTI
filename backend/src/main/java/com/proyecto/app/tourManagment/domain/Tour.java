package com.proyecto.app.tourManagment.domain;

import com.proyecto.app.catalog.domain.Category;
import com.proyecto.app.common.Environment;
import com.proyecto.app.common.Location;
import com.proyecto.app.media.domain.Album;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tours")
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(name = "tour_categories", joinColumns = @JoinColumn(name = "tour_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "environment")
    private Environment environment;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<Itinerary> itinerary = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    private double price;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "location_city")),
            @AttributeOverride(name = "department", column = @Column(name = "location_department")),
            @AttributeOverride(name = "country", column = @Column(name = "location_country")),
            @AttributeOverride(name = "latitude", column = @Column(name = "location_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "location_longitude"))
    })
    private Location location;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "meeting_point_city")),
            @AttributeOverride(name = "department", column = @Column(name = "meeting_point_department")),
            @AttributeOverride(name = "country", column = @Column(name = "meeting_point_country")),
            @AttributeOverride(name = "latitude", column = @Column(name = "meeting_point_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "meeting_point_longitude"))
    })
    private Location meetingPoint;

    @OneToOne(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private TourOffer tourOffer;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "album_id")
    private Album album;
}