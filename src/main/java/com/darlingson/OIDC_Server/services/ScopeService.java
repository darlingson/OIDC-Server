package com.darlingson.OIDC_Server.services;

import com.darlingson.OIDC_Server.entities.Scope;
import com.darlingson.OIDC_Server.repositories.ScopeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScopeService {

    private final ScopeRepository scopeRepository;

    public ScopeService(ScopeRepository scopeRepository) {
        this.scopeRepository = scopeRepository;
    }

    public Scope createScope(Scope scope) {
        if (scopeRepository.existsByName(scope.getName())) {
            throw new IllegalArgumentException("Scope with name '" + scope.getName() + "' already exists");
        }
        return scopeRepository.save(scope);
    }

    public List<Scope> getAllScopes() {
        return scopeRepository.findAllByOrderByNameAsc();
    }

    public Optional<Scope> getScopeByName(String name) {
        return scopeRepository.findByName(name);
    }

    public List<Scope> getDefaultScopes() {
        return scopeRepository.findByIsDefault(true);
    }

    public Scope updateScope(UUID id, Scope scopeDetails) {
        Scope scope = scopeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Scope not found with id: " + id));

        if (!scope.getName().equals(scopeDetails.getName()) &&
            scopeRepository.existsByName(scopeDetails.getName())) {
            throw new IllegalArgumentException("Scope with name '" + scopeDetails.getName() + "' already exists");
        }

        scope.setName(scopeDetails.getName());
        scope.setDescription(scopeDetails.getDescription());
        scope.setUserProperty(scopeDetails.getUserProperty());
        scope.setDefault(scopeDetails.isDefault());

        return scopeRepository.save(scope);
    }

    public void deleteScope(UUID id) {
        if (!scopeRepository.existsById(id)) {
            throw new IllegalArgumentException("Scope not found with id: " + id);
        }
        scopeRepository.deleteById(id);
    }
}