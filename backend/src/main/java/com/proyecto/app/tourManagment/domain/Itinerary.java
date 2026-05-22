package com.proyecto.app.tourManagment.domain;

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

    @Column(name = "tourist_place_id", nullable = false)
    private Long touristPlaceId;

    @Column(nullable = false)
    private int position;

    public Itinerary(Tour tour, Long touristPlaceId, int position) {
        this.tour = tour;
        this.touristPlaceId = touristPlaceId;
        this.position = position;
    }
}