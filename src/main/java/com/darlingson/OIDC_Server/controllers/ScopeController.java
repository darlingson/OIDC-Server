package com.darlingson.OIDC_Server.controllers;

import com.darlingson.OIDC_Server.entities.Scope;
import com.darlingson.OIDC_Server.services.ScopeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scopes")
public class ScopeController {

    private final ScopeService scopeService;

    public ScopeController(ScopeService scopeService) {
        this.scopeService = scopeService;
    }

    @PostMapping
    public Scope createScope(@RequestBody Scope scope) {
        return scopeService.createScope(scope);
    }

    @GetMapping
    public List<Scope> getAllScopes() {
        return scopeService.getAllScopes();
    }

    @GetMapping("/default")
    public List<Scope> getDefaultScopes() {
        return scopeService.getDefaultScopes();
    }

    @GetMapping("/{name}")
    public Scope getScopeByName(@PathVariable String name) {
        return scopeService.getScopeByName(name)
            .orElseThrow(() -> new IllegalArgumentException("Scope not found: " + name));
    }

    @PutMapping("/{id}")
    public Scope updateScope(@PathVariable UUID id, @RequestBody Scope scope) {
        return scopeService.updateScope(id, scope);
    }

    @DeleteMapping("/{id}")
    public void deleteScope(@PathVariable UUID id) {
        scopeService.deleteScope(id);
    }
}