package com.proyecto.app.userManagment.config;

import com.proyecto.app.common.User;
import com.proyecto.app.security.domain.Credential;
import com.proyecto.app.security.repository.CredentialRepository;
import com.proyecto.app.userManagment.domain.Contact;
import com.proyecto.app.userManagment.domain.UserProfile;
import com.proyecto.app.userManagment.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CredentialRepository credentialRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (credentialRepository.existsByEmail("admin@gmail.com")) return;

        
        UserProfile adminProfile = new UserProfile(
                "Administrador",
                "1053489671",
                "ADMINISTRATOR"
        );
        Contact contact = new Contact("3123805426", adminProfile);
        adminProfile.addContact(contact);

        
        User adminUser = new User(adminProfile);
        userRepository.save(adminUser); 

        Credential credential = new Credential(
                "admin@gmail.com",
                passwordEncoder.encode("admin123"),
                "ADMINISTRATOR",
                adminUser.getId()
        );
        credentialRepository.save(credential);

        System.out.println("Admin creado: admin@gmail.com / admin123");
    }
}