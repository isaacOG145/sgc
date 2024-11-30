package utez.edu._b.sgc.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import utez.edu._b.sgc.role.model.Role;
import utez.edu._b.sgc.role.model.RoleRepository;
import utez.edu._b.sgc.users.model.User;
import utez.edu._b.sgc.users.model.UserRepository;

import java.util.Optional;


@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            Optional<Role> optionalRole = roleRepository.findByName("ROLE_ADMIN");
            if (!optionalRole.isPresent()) {
                Role adminRole = new Role("ROLE_ADMIN");
                roleRepository.saveAndFlush(adminRole);

                Optional<User> optionalUser = userRepository.findByEmail("admin@domain.com");
                if (!optionalUser.isPresent()) {
                    // Crear usuario admin
                    User adminUser = new User("Admin", "User", "admin@domain.com", "1234567890", passwordEncoder.encode("adminPassword"));
                    adminUser.setStatus(true);
                    adminUser.setRole(adminRole);  // Asignar el rol admin
                    userRepository.saveAndFlush(adminUser);
                }
            }

            // Crear o asegurar que el rol de 'consultas' o sin rol específico existe (si es necesario)
            optionalRole = roleRepository.findByName("ROLE_USER");
            if (!optionalRole.isPresent()) {
                Role queryRole = new Role("ROLE_USER");
                roleRepository.saveAndFlush(queryRole);

                Optional<User> optionalUser = userRepository.findByEmail("queryuser@domain.com");
                if (!optionalUser.isPresent()) {
                    // Crear usuario con acceso solo para consultas (sin rol o con un rol específico)
                    User queryUser = new User("Query", "User", "queryuser@domain.com", "0987654321", passwordEncoder.encode("queryPassword"));
                    queryUser.setRole(queryRole);  // Asignar el rol de solo consultas
                    userRepository.saveAndFlush(queryUser);
                }
            }
        };
    }
}