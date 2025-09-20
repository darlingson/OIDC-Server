package com.darlingson.OIDC_Server.repositories;

import com.darlingson.OIDC_Server.entities.AuthorizationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorizationCodeRepository extends JpaRepository<AuthorizationCode, String> {
    Optional<AuthorizationCode> findByCode(String code);
}