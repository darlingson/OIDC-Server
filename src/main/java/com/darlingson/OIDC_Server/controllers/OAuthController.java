package com.darlingson.OIDC_Server.controllers;

import com.darlingson.OIDC_Server.dto.TokenResponse;
import com.darlingson.OIDC_Server.entities.AuthorizationCode;
import com.darlingson.OIDC_Server.entities.ClientApplication;
import com.darlingson.OIDC_Server.entities.User;
import com.darlingson.OIDC_Server.repositories.AuthorizationCodeRepository;
import com.darlingson.OIDC_Server.repositories.ClientApplicationRepository;
import com.darlingson.OIDC_Server.repositories.UserRepository;
import com.darlingson.OIDC_Server.services.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    private final ClientApplicationRepository clientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final JwtService jwtService;

    public OAuthController(ClientApplicationRepository clientRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthorizationCodeRepository authorizationCodeRepository,
                          JwtService jwtService) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorizationCodeRepository = authorizationCodeRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/authorize/info")
    public Map<String, Object> getAppInfo(@RequestParam String client_id) {
        ClientApplication client = clientRepository.findByClientId(client_id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid client ID"));

        if (!client.isActive()) {
            throw new IllegalArgumentException("Client application is not active");
        }

        return Map.of(
            "client_id", client.getClientId(),
            "client_name", client.getClientName(),
            "scopes", client.getScopes(),
            "redirect_uris", client.getRedirectUris()
        );
    }

    @PostMapping("/authorize")
    public Map<String, Object> authorize(
            @RequestParam String client_id,
            @RequestParam String redirect_uri,
            @RequestParam String response_type,
            @RequestParam String scope,
            @RequestParam(required = false) String state,
            @RequestParam String username,
            @RequestParam String password) {

        ClientApplication client = clientRepository.findByClientIdWithCollections(client_id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid client ID"));

        if (!client.isActive()) {
            throw new IllegalArgumentException("Client application is not active");
        }

        if (!client.getRedirectUris().contains(redirect_uri)) {
            throw new IllegalArgumentException("Invalid redirect URI");
        }

        if (!"code".equals(response_type)) {
            throw new IllegalArgumentException("Unsupported response type");
        }

        var user = userRepository.findByEmail(username)
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        Set<String> requestedScopes = Arrays.stream(scope.split(" ")).collect(Collectors.toSet());
        Set<String> clientScopes = client.getScopes();

        if (!clientScopes.containsAll(requestedScopes)) {
            throw new IllegalArgumentException("Invalid scopes requested");
        }

        String grantedScopes = String.join(" ", requestedScopes);

        String authorizationCode = UUID.randomUUID().toString().replace("-", "");

        AuthorizationCode codeEntity = new AuthorizationCode();
        codeEntity.setCode(authorizationCode);
        codeEntity.setClient(client);
        codeEntity.setUser(user);
        codeEntity.setRedirectUri(redirect_uri);
        codeEntity.setScope(grantedScopes);
        codeEntity.setExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000));
        codeEntity.setUsed(false);

        authorizationCodeRepository.save(codeEntity);
        Map<String, Object> response = new HashMap<>();
        response.put("code", authorizationCode);
        response.put("redirect_uri", redirect_uri);
        response.put("scope", grantedScopes);

        if (state != null) {
            response.put("state", state);
        }

        return response;
    }

    @PostMapping("/token")
    public Map<String, Object> token(
            @RequestParam String grant_type,
            @RequestParam String code,
            @RequestParam String redirect_uri,
            @RequestParam String client_id,
            @RequestParam String client_secret) {

        ClientApplication client = clientRepository.findByClientId(client_id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid client ID"));

        if (!passwordEncoder.matches(client_secret, client.getClientSecret())) {
            throw new IllegalArgumentException("Invalid client credentials");
        }

        AuthorizationCode authCode = authorizationCodeRepository.findByCode(code)
            .orElseThrow(() -> new IllegalArgumentException("Invalid authorization code"));

        if (authCode.isUsed()) {
            throw new IllegalArgumentException("Authorization code already used");
        }

        if (authCode.getExpiresAt().before(new Date())) {
            throw new IllegalArgumentException("Authorization code expired");
        }

        if (!authCode.getClient().getId().equals(client.getId())) {
            throw new IllegalArgumentException("Code not issued for this client");
        }

        if (!authCode.getRedirectUri().equals(redirect_uri)) {
            throw new IllegalArgumentException("Redirect URI mismatch");
        }

        authCode.setUsed(true);
        authorizationCodeRepository.save(authCode);

        User user = authCode.getUser();
        String accessToken = jwtService.generateAccessToken(user, authCode.getScope());
        String refreshToken = jwtService.generateRefreshToken(user);

        TokenResponse response = new TokenResponse(
            accessToken,
            "Bearer",
            3600,
            refreshToken,
            authCode.getScope()
        );

        return response.toMap();
    }

    @GetMapping("/userinfo")
    public Map<String, Object> userInfo(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid access token");
        }

        String accessToken = authHeader.substring(7);

        if (!jwtService.validateToken(accessToken)) {
            throw new IllegalArgumentException("Invalid or expired access token");
        }

        Integer userId = jwtService.extractUserId(accessToken);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Set<String> grantedScopes = jwtService.extractScopes(accessToken);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("sub", user.getId().toString());

        if (grantedScopes.contains("email")) {
            userInfo.put("email", user.getEmail());
            userInfo.put("email_verified", true);
        }

        if (grantedScopes.contains("profile")) {
            userInfo.put("name", user.getFullName());
            userInfo.put("preferred_username", user.getEmail());
            userInfo.put("given_name", user.getFullName());
            userInfo.put("family_name", user.getFullName());
        }

        if (grantedScopes.contains("roles")) {
            userInfo.put("roles", user.getRole().getName().name());
        }

        return userInfo;
    }
}