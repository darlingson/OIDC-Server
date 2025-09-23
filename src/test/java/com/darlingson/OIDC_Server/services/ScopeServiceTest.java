package com.darlingson.OIDC_Server.services;

import com.darlingson.OIDC_Server.entities.Scope;
import com.darlingson.OIDC_Server.repositories.ScopeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScopeServiceTest {

    @Mock
    private ScopeRepository scopeRepository;  // Fake the repo – we don't hit a real DB

    @InjectMocks
    private ScopeService scopeService;

    private Scope testScope;

    @BeforeEach
    void setUp() {
        testScope = new Scope();
        testScope.setName("read:user");
        testScope.setDescription("Read user info");
        testScope.setUserProperty("email");
        testScope.setDefault(false);
    }

    @Test
    void createScope_ShouldSaveAndReturnScope_WhenNameIsUnique() {
        when(scopeRepository.existsByName("read:user")).thenReturn(false);
        when(scopeRepository.save(any(Scope.class))).thenReturn(testScope);

        Scope result = scopeService.createScope(testScope);

        assertNotNull(result);
        assertEquals("read:user", result.getName());
        verify(scopeRepository).save(testScope);
    }

    @Test
    void createScope_ShouldThrowException_WhenNameAlreadyExists() {
        when(scopeRepository.existsByName("read:user")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> scopeService.createScope(testScope)
        );
        assertEquals("Scope with name 'read:user' already exists", exception.getMessage());
        verify(scopeRepository, never()).save(any());
    }
}