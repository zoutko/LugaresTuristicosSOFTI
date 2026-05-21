package com.proyecto.app.tourManagment.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DiscountResponse {
    private Long id;
    private String userTypeName;
    private double percentage;

    public DiscountResponse(Long id, String userTypeName, double percentage) {
        this.id = id;
        this.userTypeName = userTypeName;
        this.percentage = percentage;
    }
}
