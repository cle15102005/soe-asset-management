package vn.edu.hust.soict.soe.assetmanagement.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * JWT configuration — reads values from application.yml
 * (secret key and token expiration time).
 * Used in JwtUtil.java for token generation and validation.
 * Note: In production, the secret should be stored securely (e.g., environment variable or vault)
 * and not hardcoded in application.yml. For this project, it's acceptable for simplicity.
 */
@Getter
@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;
}