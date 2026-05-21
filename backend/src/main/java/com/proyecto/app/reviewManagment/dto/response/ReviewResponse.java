package com.proyecto.app.reviewManagment.dto.response;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewResponse {
    private Long id;
    private Long authorId;
    private String authorName;
    private Long tourId;
    private int rating;
    private LocalDate publicationDate;
    private String comment;

    public ReviewResponse(Long id, Long authorId, String authorName,
                          Long tourId, int rating,
                          LocalDate publicationDate, String comment) {
        this.id = id;
        this.authorId = authorId;
        this.authorName = authorName;
        this.tourId = tourId;
        this.rating = rating;
        this.publicationDate = publicationDate;
        this.comment = comment;
    }
}
