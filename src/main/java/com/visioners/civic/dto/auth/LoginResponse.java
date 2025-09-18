package com.visioners.civic.dto.auth;

import java.time.Instant;

import lombok.Builder;

@Builder
public record LoginResponse(String mobileNuber, String accessToken, String refreshToken, Instant timestamp) {

} 
