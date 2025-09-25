package com.civic.visioners.issue.dto;

import com.visioners.civic.issue.model.IssueSeverity;
import com.visioners.civic.issue.model.IssueStatus;
import com.visioners.civic.issue.model.Location;

public record ComplaintDto(
        Long id,
        String imageUrl,
        String description,
        String category,
        String subCategory,
        IssueSeverity severity,
        IssueStatus status,
        String assignedStaffName,
        String raisedByName,
        String departmentName,
        String districtName,
        String blockName,
        Location location,
        String resolutionImageUrl
) {}
