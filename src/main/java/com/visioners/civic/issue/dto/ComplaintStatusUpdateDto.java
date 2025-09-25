package com.visioners.civic.issue.dto;


import com.visioners.civic.issue.model.IssueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintStatusUpdateDto {
    private IssueStatus status; // IN_PROGRESS, RESOLVED, REJECTED
    private String resolutionNote; // Optional note
}

