package com.proyecto.app.tourManagment.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TourOfferResponse {
    private Long id;
    private double basePrice;
    private List<DiscountResponse> discounts;

    public TourOfferResponse(Long id, double basePrice, List<DiscountResponse> discounts) {
        this.id = id;
        this.basePrice = basePrice;
        this.discounts = discounts;
    }
}