package com.proyecto.app.tourManagment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DiscountRequest {

    private Long userTypeId;
    private double percentage;
}
