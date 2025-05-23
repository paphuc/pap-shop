package com.pap_shop.configuration;

import com.pap_shop.entity.Roles;
import com.pap_shop.entity.User;
import com.pap_shop.repository.RoleRepository;
import com.pap_shop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Configuration class for application initialization.
 *
 * This configuration runs automatically at application startup.
 * It ensures that the default roles ("ADMIN" and "USER") exist,
 * and creates a default admin user if one does not already exist.
 *
 * Admin user credentials (default):
 * - username: admin
 * - password: 1234
 *
 * Note: It's recommended to change the default password after the first login.
 */
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {

            Roles adminRole = roleRepository.findByRole("ADMIN")
                    .orElseGet(() -> roleRepository.save(new Roles(null, "ADMIN")));


            roleRepository.findByRole("USER")
                    .orElseGet(() -> roleRepository.save(new Roles(null, "USER")));


            if (userRepository.findByUsername("admin").isEmpty()) {
                User adminUser = User.builder()
                        .name("admin")
                        .username("admin")
                        .password(passwordEncoder.encode("1234"))
                        .email("admin@example.com")
                        .phone("0000000000")
                        .createdAt(Timestamp.from(Instant.now()))
                        .role(adminRole)
                        .build();

                userRepository.save(adminUser);
                log.warn("admin user has been created with default password: 1234, please change it");
            }
        };
    }
}

