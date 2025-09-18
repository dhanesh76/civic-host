package com.visioners.civic.dto.auth;

import java.time.Instant;

public record RegisterResponse(String mobileNumber, Instant createdAt) {
}