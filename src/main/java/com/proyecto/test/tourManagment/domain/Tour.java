package com.proyecto.test.tourManagment.domain;

import java.util.List;

import com.proyecto.test.touristPlaceManagment.domain.TouristPlace;
import com.proyecto.test.utils.Category;
import com.proyecto.test.utils.Environment;
import com.proyecto.test.utils.Location;

public class Tour {
    private long id;
    private String name;
    private Category categories;
    private Environment enviroment;
    private List<TouristPlace> itinerary;
    private String description;
    private String recommendations;
    private List<Rate> rates;
    private Location location;
    private Location meetingPoint;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Category getCategories() {
        return categories;
    }
    public void setCategories(Category categories) {
        this.categories = categories;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }
    public Environment getEnviroment() {
        return enviroment;
    }
    public void setEnviroment(Environment enviroment) {
        this.enviroment = enviroment;
    }
    public List<TouristPlace> getItinerary() {
        return itinerary;
    }
    public void setItinerary(List<TouristPlace> itinerary) {
        this.itinerary = itinerary;
    }
    public List<Rate> getRates() {
        return rates;
    }
    public void setRates(List<Rate> rates) {
        this.rates = rates;
    }
    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }
    public Location getMeetingPoint() {
        return meetingPoint;
    }
    public void setMeetingPoint(Location meetingPoint) {
        this.meetingPoint = meetingPoint;
    }
    
    
}
