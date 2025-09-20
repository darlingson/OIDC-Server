package com.darlingson.OIDC_Server.config;

import com.darlingson.OIDC_Server.entities.Role;
import com.darlingson.OIDC_Server.entities.RoleEnum;
import com.darlingson.OIDC_Server.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            for (RoleEnum roleEnum : RoleEnum.values()) {
                if (!roleRepository.findByName(roleEnum).isPresent()) {
                    Role role = new Role();
                    role.setName(roleEnum);
                    role.setDescription(roleEnum.name() + " role");
                    roleRepository.save(role);
                }
            }
        };
    }
}