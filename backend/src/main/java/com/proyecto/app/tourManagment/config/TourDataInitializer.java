package com.proyecto.app.tourManagment.config;

import com.proyecto.app.tourManagment.domain.UserType;
import com.proyecto.app.tourManagment.repository.UserTypeRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class TourDataInitializer implements ApplicationRunner {

    private final UserTypeRepository userTypeRepository;

    public TourDataInitializer(UserTypeRepository userTypeRepository) {
        this.userTypeRepository = userTypeRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        seedUserType("STUDENT", "Descuento para estudiantes con carnet vigente");
        seedUserType("SENIOR", "Descuento para adultos mayores de 60 años");
        seedUserType("CHILD", "Descuento para niños menores de 12 años");
        seedUserType("GENERAL", "Tarifa general sin descuento especial");
    }

    private void seedUserType(String name, String description) {
        if (!userTypeRepository.existsByName(name)) {
            UserType userType = new UserType();
            userType.setName(name);
            userType.setDescription(description);
            userTypeRepository.save(userType);
            System.out.println("UserType creado: " + name);
        }
    }
}