package com.visioners.civic.auth.dto;

import java.time.Instant;

public record RegisterResponse(String mobileNumber, Instant createdAt) {
}