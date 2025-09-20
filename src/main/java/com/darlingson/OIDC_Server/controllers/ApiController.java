package com.darlingson.OIDC_Server.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/user/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public String userProfile() {
        return "User profile accessible to all authenticated users";
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard() {
        return "Admin dashboard - only for ADMIN users";
    }

    @GetMapping("/super-admin/settings")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String superAdminSettings() {
        return "Super Admin settings - only for SUPER_ADMIN users";
    }
}