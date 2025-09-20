package com.darlingson.OIDC_Server.controllers;

import com.darlingson.OIDC_Server.entities.ClientApplication;
import com.darlingson.OIDC_Server.repositories.ClientApplicationRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
public class ClientRegistrationController {

    private final ClientApplicationRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientRegistrationController(ClientApplicationRepository clientRepository,
                                      PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ClientRegistrationResponse registerClient(@RequestBody ClientRegistrationRequest request) {
        if (clientRepository.existsByClientName(request.getClientName())) {
            throw new IllegalArgumentException("Client name already exists");
        }

        String clientId = generateClientId();
        String clientSecret = generateClientSecret();

        ClientApplication client = new ClientApplication();
        client.setClientName(request.getClientName());
        client.setClientId(clientId);
        client.setClientSecret(passwordEncoder.encode(clientSecret));
        client.setRedirectUris(request.getRedirectUris());
        client.setScopes(request.getScopes());
        client.setGrantTypes(Set.of("authorization_code", "refresh_token")); // Default grant types

        clientRepository.save(client);

        return new ClientRegistrationResponse(
            clientId,
            clientSecret,
            client.getClientName(),
            client.getRedirectUris(),
            client.getScopes()
        );
    }

    private String generateClientId() {
        return "client_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateClientSecret() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static class ClientRegistrationRequest {
        private String clientName;
        private Set<String> redirectUris;
        private Set<String> scopes;

        public String getClientName() { return clientName; }
        public void setClientName(String clientName) { this.clientName = clientName; }
        public Set<String> getRedirectUris() { return redirectUris; }
        public void setRedirectUris(Set<String> redirectUris) { this.redirectUris = redirectUris; }
        public Set<String> getScopes() { return scopes; }
        public void setScopes(Set<String> scopes) { this.scopes = scopes; }
    }

    public static class ClientRegistrationResponse {
        private String clientId;
        private String clientSecret;
        private String clientName;
        private Set<String> redirectUris;
        private Set<String> scopes;

        public ClientRegistrationResponse(String clientId, String clientSecret, String clientName,
                                        Set<String> redirectUris, Set<String> scopes) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.clientName = clientName;
            this.redirectUris = redirectUris;
            this.scopes = scopes;
        }

        public String getClientId() { return clientId; }
        public String getClientSecret() { return clientSecret; }
        public String getClientName() { return clientName; }
        public Set<String> getRedirectUris() { return redirectUris; }
        public Set<String> getScopes() { return scopes; }
    }
}