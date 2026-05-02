package com.proyecto.test.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Location {

    @Column(name = "city")
    private String city;

    @Column(name = "department")
    private String department;

    @Column(name = "country")
    private String country;

    @Column(name = "latitude")
    private Double latitude;

    public Location() {}

    public String getFullLocation() {
        return city + ", " + department + ", " + country;
    }

    public boolean isSameCity(Location other) {
        return this.city.equalsIgnoreCase(other.city);
    }     
}
