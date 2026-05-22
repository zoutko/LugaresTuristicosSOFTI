package com.proyecto.app.userManagment.domain;

import com.proyecto.app.common.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "saved_tours",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "tour_id"}))
public class SavedTour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "tour_id", nullable = false)
    private Long tourId;

    public SavedTour(User user, Long tourId) {
        this.user = user;
        this.tourId = tourId;
    }
}