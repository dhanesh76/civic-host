package com.civic.visioners.auth.dto;

/**
 * Result of OTP validation.
 */
public record OtpValidationResult(
    boolean valid,
    String message
) {}
