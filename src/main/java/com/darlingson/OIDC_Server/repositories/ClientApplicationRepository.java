package com.darlingson.OIDC_Server.repositories;

import com.darlingson.OIDC_Server.entities.ClientApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientApplicationRepository extends JpaRepository<ClientApplication, UUID> {
    Optional<ClientApplication> findByClientId(String clientId);
    Optional<ClientApplication> findByClientName(String clientName);
    boolean existsByClientId(String clientId);
    boolean existsByClientName(String clientName);
}