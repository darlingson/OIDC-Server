package com.darlingson.OIDC_Server.repositories;

import com.darlingson.OIDC_Server.entities.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScopeRepository extends JpaRepository<Scope, UUID> {
    Optional<Scope> findByName(String name);
    boolean existsByName(String name);
    List<Scope> findByIsDefault(boolean isDefault);
    List<Scope> findAllByOrderByNameAsc();
}