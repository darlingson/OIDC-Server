package com.darlingson.OIDC_Server.repositories;

import com.darlingson.OIDC_Server.entities.Role;
import com.darlingson.OIDC_Server.entities.RoleEnum;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByName(RoleEnum name);
}