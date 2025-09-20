package com.visioners.civic.auth.dto;

import java.util.Set;
import com.visioners.civic.role.entity.Role;

/**
 * Temp object stored in Redis until OTP verification.
 */
public record RegisterSession(
    String mobileNumber,
    String encodedPassword,
    Set<Role> roles
) {}
