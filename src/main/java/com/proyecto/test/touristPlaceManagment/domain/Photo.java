package com.proyecto.test.touristPlaceManagment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Photo {

    private String filePath;
    private String description;

    public Photo() {
    }

    public boolean isValid() {
        return filePath != null && !filePath.isEmpty();
    }

    public String getFileName() {
        if (filePath == null)
            return "";
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }
}