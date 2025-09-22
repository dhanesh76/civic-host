public class ComplaintCreateDto {
    @NotBlank
    private String description;
    private IssueCategory category;
    private IssueSubCategory subCategory;
    private LocationDto location; // contains latitude, longitude, subAdminArea, etc.
    private Long raisedById;
}

