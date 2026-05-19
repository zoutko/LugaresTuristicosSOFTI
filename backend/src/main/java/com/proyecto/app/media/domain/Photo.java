package com.proyecto.app.media.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "photos")
@Getter
@Setter
@NoArgsConstructor
public class Photo {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "photo_description")
    private String description;

    public boolean isValid() {
        return filePath != null && !filePath.isBlank();
    }

    public String getFileName() {
        if (filePath == null) return "";
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }
}
