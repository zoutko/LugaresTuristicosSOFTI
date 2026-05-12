package com.proyecto.test.userManagment.config;

import com.proyecto.test.common.User;
import com.proyecto.test.security.domain.Credential;
import com.proyecto.test.security.repository.CredentialRepository;
import com.proyecto.test.userManagment.domain.Contact;
import com.proyecto.test.userManagment.domain.UserProfile;
import com.proyecto.test.userManagment.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
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
                "000000000",
                "ADMINISTRATOR"
        );
        Contact contact = new Contact("3000000000", adminProfile);
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