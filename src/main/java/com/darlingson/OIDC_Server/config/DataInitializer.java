package com.darlingson.OIDC_Server.config;

import com.darlingson.OIDC_Server.entities.Role;
import com.darlingson.OIDC_Server.entities.RoleEnum;
import com.darlingson.OIDC_Server.entities.Scope;
import com.darlingson.OIDC_Server.repositories.RoleRepository;
import com.darlingson.OIDC_Server.repositories.ScopeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, ScopeRepository scopeRepository) {
        return args -> {
            initRoles(roleRepository);
            initScopes(scopeRepository);
        };
    }

    private void initRoles(RoleRepository roleRepository) {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleRepository.findByName(roleEnum).isEmpty()) {
                Role role = new Role();
                role.setName(roleEnum);
                role.setDescription(roleEnum.name() + " role");
                roleRepository.save(role);
            }
        }
    }

    private void initScopes(ScopeRepository scopeRepository) {
        List<Scope> defaultScopes = Arrays.asList(
            createScope("openid", "OpenID Connect authentication", "id", true),
            createScope("profile", "User profile information", "fullName", true),
            createScope("email", "Email address", "email", true),
            createScope("address", "Physical address", "address", false),
            createScope("phone", "Phone number", "phone", false),
            createScope("offline_access", "Offline access with refresh tokens", null, false)
        );

        for (Scope scope : defaultScopes) {
            if (scopeRepository.findByName(scope.getName()).isEmpty()) {
                scopeRepository.save(scope);
            }
        }
    }

    private Scope createScope(String name, String description, String userProperty, boolean isDefault) {
        Scope scope = new Scope();
        scope.setName(name);
        scope.setDescription(description);
        scope.setUserProperty(userProperty);
        scope.setDefault(isDefault);
        return scope;
    }
}