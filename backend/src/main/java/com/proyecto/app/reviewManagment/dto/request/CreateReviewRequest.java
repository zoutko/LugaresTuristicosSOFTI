package com.proyecto.app.reviewManagment.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewRequest {

    private Long tourId;
    private int rating;
    private String comment;
}