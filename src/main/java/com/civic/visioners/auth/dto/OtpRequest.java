package com.civic.visioners.auth.dto;

import com.visioners.civic.auth.model.OtpPurpose;

public record OtpRequest(
    String mobileNumber,
    OtpPurpose purpose
) {}
