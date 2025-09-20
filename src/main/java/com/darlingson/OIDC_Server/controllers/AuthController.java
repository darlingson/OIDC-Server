package com.darlingson.OIDC_Server.controllers;

import com.darlingson.OIDC_Server.entities.Role;
import com.darlingson.OIDC_Server.entities.RoleEnum;
import com.darlingson.OIDC_Server.entities.User;
import com.darlingson.OIDC_Server.repositories.RoleRepository;
import com.darlingson.OIDC_Server.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                         RoleRepository roleRepository,
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public Map<String, Object> registerUser(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password) {

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        Role userRole = roleRepository.findByName(RoleEnum.USER)
            .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(userRole);

        User savedUser = userRepository.save(user);

        return Map.of(
            "message", "User registered successfully",
            "user_id", savedUser.getId(),
            "email", savedUser.getEmail(),
            "fullName", savedUser.getFullName()
        );
    }

    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestParam String email,
            @RequestParam String password) {

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return Map.of(
            "message", "Login successful",
            "user_id", user.getId(),
            "email", user.getEmail(),
            "fullName", user.getFullName(),
            "role", user.getRole().getName().name()
        );
    }

    @GetMapping("/profile")
    public Map<String, Object> getUserProfile(@RequestParam String email) {
        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return Map.of(
            "user_id", user.getId(),
            "email", user.getEmail(),
            "fullName", user.getFullName(),
            "role", user.getRole().getName().name(),
            "created_at", user.getCreatedAt()
        );
    }
}