package com.visioners.civic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visioners.civic.dto.auth.LoginRequest;
import com.visioners.civic.dto.auth.LoginResponse;
import com.visioners.civic.dto.auth.RegisterRequest;
import com.visioners.civic.dto.auth.RegisterResponse;
import com.visioners.civic.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class Authentication {

    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse registerResponse = authenticationService.register(registerRequest);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(registerResponse);
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(LoginRequest loginRequest, AuthenticationManager authenticationManager){

        LoginResponse loginResponse = authenticationService.login(loginRequest, authenticationManager);
        return ResponseEntity.ok().body(loginResponse);
    }
}
