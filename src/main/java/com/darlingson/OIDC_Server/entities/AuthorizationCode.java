package com.darlingson.OIDC_Server.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "authorization_codes")
public class AuthorizationCode {
    @Id
    private String code;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientApplication client;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String redirectUri;
    private String scope;
    private Date expiresAt;
    private boolean used;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public ClientApplication getClient() { return client; }
    public void setClient(ClientApplication client) { this.client = client; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getRedirectUri() { return redirectUri; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public Date getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}