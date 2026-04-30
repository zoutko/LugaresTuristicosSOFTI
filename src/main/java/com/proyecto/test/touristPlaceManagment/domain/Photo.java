package com.proyecto.test.touristPlaceManagment.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Photo {

    private int id;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "description")
    private String description;

    public Photo() {}

    public boolean isValid() {
        return filePath != null && !filePath.isEmpty();
    }

    public String getFileName() {
        if (filePath == null) return "";
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }
        
}
