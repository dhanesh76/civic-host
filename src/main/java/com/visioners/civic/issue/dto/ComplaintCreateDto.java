    package com.visioners.civic.issue.dto;

    import com.visioners.civic.issue.model.IssueCategory;
    import com.visioners.civic.issue.model.IssueSubCategory;
    import com.visioners.civic.issue.model.Location;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import lombok.AllArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ComplaintCreateDto {
        private String description;
        private IssueCategory category;
        private IssueSubCategory subCategory; // fixed typo
        private Location location; // latitude, longitude, etc.
    private Long departmentId;
    }
