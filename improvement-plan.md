# MILESTONE 1 — Token Foundation Redesign (ID Token + RSA + JWKS)

## Goal

Move from “JWT access token only (HMAC)” → to **OIDC-compliant identity system using RSA-signed tokens**

---

## Tasks

### 1.1 Define ID Token model

Create a standard OIDC ID token claims set:

**Required claims:**

* `iss` → issuer (your auth server URL)
* `sub` → user ID (stable identifier)
* `aud` → client_id
* `exp` → expiration
* `iat` → issued at
* `auth_time` → login time
* `nonce` → prevent replay attacks

**Optional but recommended:**

* `email`
* `email_verified`
* `name`
* `preferred_username`

---

### 1.2 Separate token types

Refactor `JwtService` into:

* `AccessTokenService`
* `IdTokenService`
* `RefreshTokenService`

---

### 1.3 Replace HMAC with RSA

Replace:

`HS256 secret key`

with:

RSA keypair:

* private key → sign tokens
* public key → exposed via JWKS

---

### 1.4 Build JWKS endpoint

Create:

```
GET /oauth2/jwks
```

Returns:

```json
{
  "keys": [
    {
      "kty": "RSA",
      "kid": "key-1",
      "use": "sig",
      "alg": "RS256",
      "n": "...",
      "e": "AQAB"
    }
  ]
}
```

---

## Deliverable

* ID Token generator (RS256)
* JWKS endpoint working
* Access + ID tokens separated

---

## Blockers

* Understanding RSA key encoding (PEM → JWK)
* Key management strategy (static vs rotating keys)

---

# MILESTONE 2 — Authorization Code Flow Refactor (CRITICAL)

## Goal

Fix your deviation from OAuth/OIDC:

> move from POST login → redirect-based browser flow

---

## Tasks

### 2.1 Split `/oauth/authorize`

Replace current:

`POST /oauth/authorize (username/password)`

with:

### Step 1:

```
GET /oauth2/authorize
```

Responsibilities:

* Validate:

  * client_id
  * redirect_uri
  * scope
* Store request in session
* Redirect to `/login`

---

### 2.2 Build login page

```
GET /login
POST /login
```

* Authenticate user
* Create server session
* Redirect to consent

---

### 2.3 Consent screen

```
GET /consent
POST /consent
```

User:

* sees requested scopes
* approves/denies

---

### 2.4 Issue authorization code

After consent:

```
302 redirect:
redirect_uri?code=XYZ&state=ABC
```

Store:

* client_id
* user_id
* scopes
* nonce
* expiration (10 min)
* used flag

---

## Deliverable

* Full browser-based OAuth flow
* No username/password in API calls anymore

---

## Blockers

* Spring Security session configuration
* CSRF handling on login/consent
* State/nonce tracking

---

# MILESTONE 3 — OIDC Discovery Endpoint

## Goal

Make your server “auto-discoverable” like Google/Auth0/Keycloak

---

## Tasks

Create:

```
GET /.well-known/openid-configuration
```

Return:

```json
{
  "issuer": "https://your-server.com",
  "authorization_endpoint": "/oauth2/authorize",
  "token_endpoint": "/oauth2/token",
  "userinfo_endpoint": "/oauth2/userinfo",
  "jwks_uri": "/oauth2/jwks",
  "response_types_supported": ["code"],
  "grant_types_supported": ["authorization_code", "refresh_token"],
  "scopes_supported": ["openid", "profile", "email"],
  "subject_types_supported": ["public"],
  "id_token_signing_alg_values_supported": ["RS256"]
}
```

---

## Deliverable

* Discovery endpoint working
* Client apps can auto-configure OIDC

---

## Blockers

* Consistency with actual endpoints (must match Milestone 2 exactly)

---

# MILESTONE 4 — JWT System Upgrade (RSA + ID Token integration)

## Goal

Unify token generation under real OIDC rules

---

## Tasks

### 4.1 Update `/oauth2/token`

Must now return:

```json
{
  "access_token": "...",
  "id_token": "...",
  "refresh_token": "...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "openid profile email"
}
```

---

### 4.2 Enforce ID Token issuance only when:

* scope contains `openid`

---

### 4.3 Fix claims consistency

Ensure:

* `sub` = user ID always stable
* `aud` = client_id
* `iss` = your server base URL

---

## Deliverable

* Fully OIDC-compliant token endpoint

---

## Blockers

* Token structure consistency across services
* Scope enforcement correctness

---

# MILESTONE 5 — PKCE Support (Security Upgrade)

## Goal

Secure public clients (SPA/mobile)

---

## Tasks

### 5.1 Add PKCE fields to authorize request

```
code_challenge
code_challenge_method (S256)
```

---

### 5.2 Store in AuthorizationCode entity

Add fields:

* code_challenge
* code_challenge_method

---

### 5.3 Validate in `/token`

Require:

* `code_verifier`
* verify against stored challenge

---

## Deliverable

* PKCE-compliant authorization flow

---

## Blockers

* SHA256 base64-url encoding correctness

---

# MILESTONE 6 — Revocation + Introspection + Hardening

## Goal

Make system production-grade

---

## Tasks

### 6.1 Token Revocation

```
POST /oauth2/revoke
```

Revoke:

* refresh tokens
* optionally blacklist access tokens

---

### 6.2 Introspection endpoint

```
POST /oauth2/introspect
```

Returns:

* active
* scope
* sub
* exp

---

### 6.3 Security hardening

* rate limiting on:

  * /login
  * /token
  * /authorize
* CSRF protection
* strict redirect URI matching (exact match only)
* audit logging

---

## Deliverable

* Production-ready auth server baseline

---

## Blockers

* Token revocation strategy (stateless JWT vs blacklist store)

---

# DEPENDENCY MAP (VERY IMPORTANT)

Order to be followed:

```
M1 → M2 → M3 → M4 → M5 → M6
```

### Why:

* Cannot do PKCE before authorization code flow is correct
* Cannot do OIDC discovery before endpoints stabilize
* Cannot do ID token before RSA signing exists
* Cannot finalize token endpoint before ID token exists

