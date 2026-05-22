package com.proyecto.app.catalog.controller;

import com.proyecto.app.catalog.domain.Category;
import com.proyecto.app.catalog.dto.CategoryRequest;
import com.proyecto.app.catalog.dto.CategoryResponse;
import com.proyecto.app.catalog.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .sorted(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER))
                .map(category -> new CategoryResponse(category.getId(), category.getName()))
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) {
        String name = request.getName().trim();
        Category category = categoryRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> categoryRepository.save(new Category(name)));

        return new CategoryResponse(category.getId(), category.getName());
    }
}

