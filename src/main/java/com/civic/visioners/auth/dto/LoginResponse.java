package com.civic.visioners.auth.dto;

import java.time.Instant;

import lombok.Builder;

@Builder
public record LoginResponse(String mobileNumber, String accessToken, String refreshToken, Instant timestamp) {

} 
