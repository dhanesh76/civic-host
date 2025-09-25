package com.civic.visioners.auth.dto;

import java.util.Set;

import com.civic.visioners.role.entity.Role;

/**
 * Temp object stored in Redis until OTP verification.
 */
public record RegisterSession(
    String mobileNumber,
    String encodedPassword,
    Set<Role> roles
) {}
