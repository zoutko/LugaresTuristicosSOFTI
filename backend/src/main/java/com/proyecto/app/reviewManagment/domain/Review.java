package com.proyecto.app.reviewManagment.domain;

import com.proyecto.app.common.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "tour_id", nullable = false)
    private Long tourId;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private LocalDate publicationDate;

    @Column(nullable = false, length = 1000)
    private String comment;

    public Review(User author, Long tourId, int rating, String comment) {
        this.author = author;
        this.tourId = tourId;
        this.rating = rating;
        this.comment = comment;
        this.publicationDate = LocalDate.now();
    }
}