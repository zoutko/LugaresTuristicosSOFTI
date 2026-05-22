package com.proyecto.app.reviewManagment.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateReviewRequest {
    private Integer rating;
    private String comment;
}