package com.visioners.civic.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visioners.civic.auth.dto.LoginRequest;
import com.visioners.civic.auth.dto.LoginResponse;
import com.visioners.civic.auth.dto.RegisterRequest;
import com.visioners.civic.auth.dto.RegisterResponse;
import com.visioners.civic.auth.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse registerResponse = authenticationService.register(registerRequest);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(registerResponse);
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        LoginResponse loginResponse = authenticationService.login(loginRequest);
        return ResponseEntity.ok().body(loginResponse);
    }
}
