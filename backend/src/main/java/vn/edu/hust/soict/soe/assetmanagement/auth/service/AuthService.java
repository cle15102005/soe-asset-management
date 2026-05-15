package vn.edu.hust.soict.soe.assetmanagement.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import vn.edu.hust.soict.soe.assetmanagement.auth.dto.LoginRequest;
import vn.edu.hust.soict.soe.assetmanagement.auth.dto.LoginResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles login logic:
 * 1. Authenticate credentials via Spring Security AuthenticationManager
 * 2. Load the full UserDetails
 * 3. Build JWT claims (include role for frontend use)
 * 4. Return LoginResponse with token and user info
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        // Throws BadCredentialsException if wrong password — caught by GlobalExceptionHandler
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getUsername());

        // Add role to JWT claims so frontend can read it without an extra API call
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", userDetails.getAuthorities()
                .stream()
                .map(Object::toString)
                .toList());

        String token = jwtService.generateToken(extraClaims, userDetails);

        return LoginResponse.builder()
                .token(token)
                .username(userDetails.getUsername())
                .build();
    }
}