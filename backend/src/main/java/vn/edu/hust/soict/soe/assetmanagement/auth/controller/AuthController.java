package vn.edu.hust.soict.soe.assetmanagement.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.hust.soict.soe.assetmanagement.auth.dto.LoginRequest;
import vn.edu.hust.soict.soe.assetmanagement.auth.dto.LoginResponse;
import vn.edu.hust.soict.soe.assetmanagement.auth.service.AuthService;
import vn.edu.hust.soict.soe.assetmanagement.common.ApiResponse;

/**
 * Authentication endpoints.
 * POST /api/auth/login — public, no JWT required.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login and token management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate with username and password, returns JWT token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.success("Đăng nhập thành công.", response));
    }
}