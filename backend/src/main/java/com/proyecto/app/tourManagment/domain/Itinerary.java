package com.proyecto.app.tourManagment.domain;

import com.proyecto.app.touristPlaceManagment.domain.TouristPlace;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "itinerary")
public class Itinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tourist_place_id", nullable = false)
    private TouristPlace touristPlace;

    @Column(nullable = false)
    private int position;

    public Itinerary(Tour tour, TouristPlace touristPlace, int position) {
        this.tour = tour;
        this.touristPlace = touristPlace;
        this.position = position;
    }
}