package com.proyecto.app.catalog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    public Category() {}

    public Category(String name) {
        this.name = name;
    }

    public boolean hasName(String name) {
        return this.name.equalsIgnoreCase(name);
    }
}
