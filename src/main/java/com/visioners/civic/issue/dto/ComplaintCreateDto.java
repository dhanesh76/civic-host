package com.visioners.civic.issue.dto;

import com.visioners.civic.issue.model.IssueCategory;
import com.visioners.civic.issue.model.IssueSubCategory;
import com.visioners.civic.issue.model.Location;
import jakarta.validation.constraints.NotBlank;

public record ComplaintCreateDto(
    @NotBlank
     String description,
     IssueCategory category,
     IssueSubCategory subCategory,
     Location location, // contains latitude, longitude, subAdminArea, etc.
     Long raisedById
){}

