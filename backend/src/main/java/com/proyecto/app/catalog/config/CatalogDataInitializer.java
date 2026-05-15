package com.proyecto.app.catalog.config;

import com.proyecto.app.catalog.domain.Category;
import com.proyecto.app.catalog.repository.CategoryRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CatalogDataInitializer implements ApplicationRunner {

    private final CategoryRepository categoryRepository;

    public CatalogDataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (categoryRepository.count() > 0) return;

        List.of("Historia", "Naturaleza", "Aventura", "Gastronomía", "Arte")
            .forEach(name -> categoryRepository.save(new Category(name)));
    }
}