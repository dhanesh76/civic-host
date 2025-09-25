package com.civic.visioners.auth.model;

public record OtpData(
    String otp,
    OtpPurpose purpose
) {}
