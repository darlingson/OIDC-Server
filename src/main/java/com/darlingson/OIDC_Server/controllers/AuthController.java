package com.darlingson.OIDC_Server.controllers;

import com.darlingson.OIDC_Server.entities.Role;
import com.darlingson.OIDC_Server.entities.RoleEnum;
import com.darlingson.OIDC_Server.entities.User;
import com.darlingson.OIDC_Server.repositories.RoleRepository;
import com.darlingson.OIDC_Server.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
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

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String fullName,
                              @RequestParam String email,
                              @RequestParam String password) {
        if (userRepository.existsByEmail(email)) {
            return "redirect:/auth/register?error";
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        // Assign USER role by default
        Role userRole = roleRepository.findByName(RoleEnum.USER)
            .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(userRole);

        userRepository.save(user);
        return "redirect:/auth/login?success";
    }
}