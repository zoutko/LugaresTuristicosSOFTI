package com.proyecto.test.reviewManagment.domain;

import java.util.Date;

import com.proyecto.test.common.User;
import com.proyecto.test.tourManagment.domain.Tour;

public class Review {
    
    private User author;
    private Tour associatedTour;
    private int rating;
    private Date publicationDtae;
    private String comment;
    public User getAuthor() {
        return author;
    }
    public void setAuthor(User author) {
        this.author = author;
    }
    public Tour getAssociatedTour() {
        return associatedTour;
    }
    public void setAssociatedTour(Tour associatedTour) {
        this.associatedTour = associatedTour;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public Date getPublicationDtae() {
        return publicationDtae;
    }
    public void setPublicationDtae(Date publicationDtae) {
        this.publicationDtae = publicationDtae;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }  
    
}
