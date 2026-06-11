-- ==================================================================================
-- Flyway Migration: V1__initial_schema.sql
-- Description: Creates the baseline schema based on Hibernate generation
-- ==================================================================================

CREATE TABLE `roles` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `roles_seq` (
    `next_val` BIGINT DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `users` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `full_name` VARCHAR(255) DEFAULT NULL,
    `email` VARCHAR(255) DEFAULT NULL,
    `password` VARCHAR(255) DEFAULT NULL,
    `role_id` INT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `fk_users_role` (`role_id`),
    CONSTRAINT `fk_users_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `users_seq` (
    `next_val` BIGINT DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `scopes` (
    `id` BINARY(16) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    `user_property` VARCHAR(255) DEFAULT NULL,
    `is_default` TINYINT(1) NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `clients` (
    `id` BINARY(16) NOT NULL,
    `client_name` VARCHAR(255) NOT NULL,
    `client_id` VARCHAR(255) NOT NULL,
    `client_secret` VARCHAR(255) NOT NULL,
    `active` TINYINT(1) NOT NULL DEFAULT 1,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `client_name` (`client_name`),
    UNIQUE KEY `client_id` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `client_redirect_uris` (
    `client_id` BINARY(16) NOT NULL,
    `redirect_uri` VARCHAR(255) DEFAULT NULL,
    KEY `FKcw186rt6r4drlekpenrlqdnl5` (`client_id`),
    CONSTRAINT `FKcw186rt6r4drlekpenrlqdnl5` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `client_scopes` (
    `client_id` BINARY(16) NOT NULL,
    `scope` VARCHAR(255) DEFAULT NULL,
    KEY `FKcw89a5cgrfemkmaal20fsrg3q` (`client_id`),
    CONSTRAINT `FKcw89a5cgrfemkmaal20fsrg3q` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `client_grant_types` (
    `client_id` BINARY(16) NOT NULL,
    `grant_type` VARCHAR(255) DEFAULT NULL,
    KEY `FKqr55dq5fnb0fq2l3gu6bafc94` (`client_id`),
    CONSTRAINT `FKqr55dq5fnb0fq2l3gu6bafc94` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `authorization_codes` (
    `code` VARCHAR(255) NOT NULL,
    `expires_at` DATETIME(6) DEFAULT NULL,
    `redirect_uri` VARCHAR(255) DEFAULT NULL,
    `scope` VARCHAR(255) DEFAULT NULL,
    `used` BIT(1) NOT NULL,
    `client_id` BINARY(16) DEFAULT NULL,
    `user_id` INT DEFAULT NULL,
    PRIMARY KEY (`code`),
    KEY `FK3ssljjubxnw9ysfuitmqhoava` (`client_id`),
    KEY `FKd6jnxs8b9jroxd73uddvt4jf3` (`user_id`),
    CONSTRAINT `FK3ssljjubxnw9ysfuitmqhoava` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`),
    CONSTRAINT `FKd6jnxs8b9jroxd73uddvt4jf3` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO roles_seq (next_val) VALUES (1);
INSERT INTO users_seq (next_val) VALUES (1);