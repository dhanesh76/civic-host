package com.visioners.civic.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visioners.civic.dto.auth.LoginRequest;
import com.visioners.civic.dto.auth.LoginResponse;
import com.visioners.civic.dto.auth.RegisterRequest;

@RestController
@RequestMapping("/api/auth")
public class Authentication {

    @PostMapping("/register")
    ResponseEntity<RegisterRequest> register(RegisterRequest registerRequest){
        return null;
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(LoginRequest loginRequest){
        return null;
    }
}
