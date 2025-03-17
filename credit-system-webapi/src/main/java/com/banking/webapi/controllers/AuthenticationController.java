package com.banking.webapi.controllers;

import com.banking.webapi.dtos.auth.AuthenticationRequest;
import com.banking.webapi.dtos.auth.AuthenticationResponse;
import com.banking.webapi.dtos.auth.RegisterRequest;
import com.banking.webapi.services.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kimlik doğrulama işlemlerini yöneten kontrolör sınıfı.
 * Kullanıcı kaydı, giriş ve token yenileme işlemlerini sağlar.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Kimlik doğrulama API'leri")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Yeni kullanıcı kaydı yapar.
     *
     * @param request Kayıt isteği
     * @return JWT token içeren yanıt
     */
    @PostMapping("/register")
    @Operation(summary = "Yeni kullanıcı kaydı", description = "Yeni bir kullanıcı hesabı oluşturur ve JWT token döndürür")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    /**
     * Kullanıcı girişi yapar.
     *
     * @param request Giriş isteği
     * @return JWT token içeren yanıt
     */
    @PostMapping("/login")
    @Operation(summary = "Kullanıcı girişi", description = "Kullanıcı kimlik bilgilerini doğrular ve JWT token döndürür")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    /**
     * Token yenileme işlemi yapar.
     *
     * @param request Token yenileme isteği
     * @return Yeni JWT token içeren yanıt
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "Token yenileme", description = "Yenileme token'ı kullanarak yeni bir JWT token oluşturur")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }
} 