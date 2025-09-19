OIDC Server Design with Spring Boot

This document outlines the system and feature design for building a Single Sign-On (SSO) and Authentication Server using Spring Boot. Our goal is to create a robust, secure, and extensible server that can act as an Identity Provider (IdP) for various client applications.
1. System Design
1.1. Architecture Overview

We will adopt a monolithic architecture for the initial phase, as it simplifies development and deployment for a single authentication server. However, the design will keep modularity in mind, allowing for potential future migration to a microservices approach if specific components (e.g., user management, audit logging) need to scale independently.

The core of our system will be built around Spring Security and Spring Authorization Server, which provides a robust implementation of OAuth 2.1 and OpenID Connect 1.0.

+-------------------+       +-------------------+
|                   |       |                   |
|   Client App 1    |       |   Client App 2    |
| (Web/Mobile/SPA)  |       | (Web/Mobile/SPA)  |
|                   |       |                   |
+---------+---------+       +---------+---------+
          |                             |
          | (OAuth/OIDC Flows)          |
          v                             v
+-------------------------------------------------+
|                                                 |
|           **Spring Boot Auth Server** |
|                                                 |
|  +-------------------------------------------+  |
|  |             API Gateway (Optional)        |  |
|  | (Security, Rate Limiting, Routing)        |  |
|  +-------------------------------------------+  |
|                         |                       |
|  +----------------------+-------------------+  |
|  |                      |                    |  |
|  |  Authentication      |  Authorization     |  |
|  |  Service             |  Server (OIDC/OAuth)|  |
|  |  (User Login, MFA)   |  (Token Issuance,  |  |
|  |                      |   Validation, JWKS) |  |
|  +----------------------+-------------------+  |
|                         |                       |
|  +----------------------+-------------------+  |
|  |                      |                    |  |
|  |  User Management     |  Client Management |  |
|  |  (Registration, CRUD)|  (Client Reg, Scopes)|  |
|  +----------------------+-------------------+  |
|                         |                       |
|  +----------------------+-------------------+  |
|  |                      |                    |  |
|  |  Consent Management  |  Auditing/Logging  |  |
|  |  (User Consent UI)   |  (Security Events) |  |
|  +----------------------+-------------------+  |
|                                                 |
+--------------------------+----------------------+
                           |
                           | (JDBC/JPA)
                           v
              +----------------------------+
              |                            |
              |     **Database** |
              | (PostgreSQL/MySQL/H2)      |
              | (User Data, Client Details,|
              |  Tokens, Consent)          |
              +----------------------------+

1.2. Key Components

    Spring Boot Application: The core framework for our server.

    Spring Security: Handles authentication and authorization within the server itself (e.g., securing admin endpoints, user login).

    Spring Authorization Server: Provides the OAuth 2.1 Authorization Server and OpenID Connect 1.0 Provider functionalities. This will manage:

        Authorization Endpoint (/oauth2/authorize)

        Token Endpoint (/oauth2/token)

        JWK Set Endpoint (/oauth2/jwks)

        OpenID Connect Discovery Endpoint (/.well-known/openid-configuration)

        User Info Endpoint (/userinfo)

    User Management Module:

        Stores user credentials (hashed passwords).

        Manages user attributes (e.g., name, email, roles).

        Handles user registration and password reset.

    Client Management Module:

        Registers and manages OAuth 2.0/OIDC client applications.

        Stores client ID, client secret, redirect URIs, allowed grant types, and scopes.

    Consent Management Module:

        Presents a user interface for users to grant or deny consent for requested scopes by client applications.

        Persists user consent decisions.

    Database:

        Choice: For development and initial deployment, an embedded H2 database can be used. For production, PostgreSQL or MySQL are recommended for their robustness and scalability.

        Schema: Will store:

            User details (username, hashed password, email, enabled status, roles).

            OAuth2 registered client details (client ID, client secret, redirect URIs, scopes, grant types, etc.).

            OAuth2 authorization codes, access tokens, refresh tokens, and consent information.

    Auditing and Logging:

        Utilize Spring Boot's logging capabilities (e.g., SLF4J with Logback) to capture security-relevant events (login attempts, token issuance, consent decisions, errors).

1.3. Protocols

    OAuth 2.1: The authorization framework that allows third-party applications to obtain limited access to user resources without exposing user credentials.

    OpenID Connect 1.0: An identity layer built on top of OAuth 2.1, providing identity verification and basic profile information about the end-user. This is crucial for SSO.

1.4. Security Considerations

    HTTPS/TLS: All communication between clients and the auth server, and within the auth server components, must use HTTPS to prevent eavesdropping and tampering.

    Password Hashing: Use a strong, adaptive hashing algorithm like BCrypt (provided by Spring Security) for storing user passwords. Never store plain-text passwords.

    JWT Signing: ID Tokens and Access Tokens (if JWT-based) will be digitally signed by the authorization server using a private key, allowing clients to verify their authenticity using the public key exposed via the JWKS endpoint.

    CORS (Cross-Origin Resource Sharing): Properly configure CORS headers to allow only trusted client origins to interact with the authentication server's APIs.

    Input Validation: Strict validation of all incoming requests to prevent injection attacks (SQL, XSS, etc.).

    Rate Limiting: Implement rate limiting on authentication and registration endpoints to mitigate brute-force attacks.

    Session Management: Secure session management for the user's login session with the IdP (e.g., using secure, HttpOnly cookies).

    PKCE (Proof Key for Code Exchange): Mandate PKCE for public clients (SPAs and mobile apps) using the Authorization Code Flow to prevent authorization code interception attacks.

    Token Revocation: Implement mechanisms to revoke access and refresh tokens if necessary (e.g., on logout, compromise).

1.5. Scalability and High Availability

    Stateless Services: Design the core authentication and authorization logic to be stateless, allowing easy horizontal scaling. Session management for the user's IdP login will be handled by Spring Security.

    Database Scaling: Utilize database replication and clustering for high availability and read scalability.

    Load Balancing: Deploy multiple instances of the Spring Boot Auth Server behind a load balancer to distribute traffic and ensure availability.

2. Feature Design
2.1. User Authentication

    Username/Password Login:

        A secure login page provided by the auth server.

        Validation of credentials against the user store.

        Password hashing and salting.

        Account lockout mechanisms for failed login attempts.

    Logout:

        Invalidate the user's session with the IdP.

        Optionally, revoke associated tokens.

    Multi-Factor Authentication (MFA) (Future Enhancement):

        Support for TOTP (Time-based One-Time Password) using an authenticator app.

        SMS-based MFA.

    Social Login (Future Enhancement):

        Integration with external IdPs like Google, GitHub, Facebook using their respective OAuth 2.0/OIDC implementations.

2.2. User Management

    User Registration:

        Self-service registration form.

        Email verification (optional, but recommended).

        Strong password policy enforcement.

    Password Reset/Recovery:

        "Forgot Password" flow with email-based token verification.

    User Profile Management:

        Allow users to view and update their basic profile information (e.g., name, email).

    Role-Based Access Control (RBAC):

        Define roles (e.g., ROLE_USER, ROLE_ADMIN).

        Assign roles to users.

        This will primarily be used for securing the auth server's own administrative endpoints.

2.3. OAuth 2.1 / OpenID Connect Flows

The server will support the following standard flows:

    Authorization Code Flow with PKCE (Proof Key for Code Exchange):

        Primary Flow: Recommended for confidential clients (server-side web applications) and mandatory for public clients (SPAs, mobile apps).

        Steps:

            Client redirects user to /oauth2/authorize with client_id, redirect_uri, scope, state, code_challenge, code_challenge_method.

            User authenticates with the IdP (our auth server).

            User grants consent for requested scopes (if not already consented).

            IdP redirects user back to redirect_uri with an authorization_code and state.

            Client exchanges authorization_code for access_token, id_token, and refresh_token at /oauth2/token endpoint, including code_verifier.

            Client uses access_token to call resource servers and id_token to verify user identity.

    Client Credentials Flow:

        For machine-to-machine communication where no user is involved.

        Client authenticates directly with /oauth2/token using client_id and client_secret to obtain an access_token.

    Refresh Token Flow:

        Allows clients to obtain new access_tokens without re-authenticating the user, using a refresh_token.

2.4. Token Management

    ID Token:

        JWT format, containing claims about the authenticated user (e.g., sub, aud, iss, exp, iat, auth_time, name, email).

        Signed by the auth server.

    Access Token:

        Can be a JWT (self-contained) or an opaque token (requires introspection by resource servers). We will start with JWT for simplicity.

        Contains scopes and claims relevant for authorization at resource servers.

    Refresh Token:

        Opaque token used to obtain new access tokens.

        Long-lived, securely stored.

    JWK Set Endpoint (/oauth2/jwks):

        Exposes the public keys used by the auth server to sign JWTs, allowing clients to verify the signature of ID Tokens and Access Tokens.

    User Info Endpoint (/userinfo):

        An OAuth 2.0 protected resource that returns claims about the authenticated end-user. Clients use the access token to call this endpoint.

2.5. Client Management

    Client Registration:

        Manual registration of client applications (for now, in-memory or database).

        Each client will have a unique client_id and client_secret.

        Configuration of redirect_uris, grant_types, and scopes.

    Scopes:

        Define permissions that a client can request (e.g., openid, profile, email, offline_access).

        openid is mandatory for OIDC.

        offline_access requests a refresh token.

2.6. Consent Management

    Consent UI: A user-friendly web page where users can review the scopes a client application is requesting and explicitly grant or deny access.

    Persistent Consent: Store user consent decisions in the database to avoid re-prompting for the same scopes from the same client.

3. Technology Stack (Initial)

    Backend: Java 17+, Spring Boot 3+, Spring Security, Spring Authorization Server

    Database: H2 (for development), PostgreSQL/MySQL (for production)

    Build Tool: Maven or Gradle

    Frontend (for login/consent UI): Thymeleaf (or simple HTML/CSS/JS) for server-rendered pages.

This design provides a solid foundation. We'll start by implementing the core authentication and authorization functionalities using the Authorization Code Flow with PKCE, along with basic user and client management.
