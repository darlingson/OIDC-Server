package com.darlingson.OIDC_Server.dto;

import java.util.Map;

public class TokenResponse {
    private String access_token;
    private String token_type;
    private long expires_in;
    private String refresh_token;
    private String scope;

    public TokenResponse(String access_token, String token_type, long expires_in,
                       String refresh_token, String scope) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
        this.scope = scope;
    }

    // Getters
    public String getAccess_token() { return access_token; }
    public String getToken_type() { return token_type; }
    public long getExpires_in() { return expires_in; }
    public String getRefresh_token() { return refresh_token; }
    public String getScope() { return scope; }

    public Map<String, Object> toMap() {
        return Map.of(
            "access_token", access_token,
            "token_type", token_type,
            "expires_in", expires_in,
            "refresh_token", refresh_token,
            "scope", scope
        );
    }
}