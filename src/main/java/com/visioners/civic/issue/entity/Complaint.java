package com.visioners.civic.issue.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.visioners.civic.issue.model.IssueCategory;
import com.visioners.civic.issue.model.IssueSeverity;
import com.visioners.civic.issue.model.IssueStatus;
import com.visioners.civic.issue.model.IssueSubCategory;
import com.visioners.civic.issue.model.Location;
import com.visioners.civic.user.entity.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 1000)
    String description;

    @Enumerated(EnumType.STRING)
    IssueCategory category;

    /*{
        "description":"jnenkjek",
        "category":"road",
        "subCategory":"pothole",
        "location" : {
            "latitude": 12.9716,
            "longitude": 77.5946,
            "accuracy": 5.0,
            "street": "MG Road",
            "locality": "Bangalore",
            "subLocality": "Ashok Nagar",
            "subAdminArea": "Bangalore Urban",
            "adminArea": "Karnataka",
            "postalCode": "560001",
            "country": "India",
            isoCountryCode": "IN"
        }
    }
        */
    @Enumerated(EnumType.STRING)
    IssueSubCategory subCategory;

    @Enumerated(EnumType.STRING)
    IssueSeverity severity;

    @Embedded
    Location location;  

    String imageUrl;

    @Enumerated(EnumType.STRING)
    IssueStatus status;

    @ManyToOne
    @JoinColumn(name = "raised_by_id")
    Users raisedBy;

    @ManyToOne
    @JoinColumn(name = "district_id")
    District district;

    @ManyToOne
    @JoinColumn(name = "block_id")
    Block block;

    @ManyToOne
    @JoinColumn(name = "department_id")
    Department department;

    @ManyToOne
    @JoinColumn(name = "assigned_staff_id")
    Users assignedStaff;

    @ManyToOne
    @JoinColumn(name = "assigned_by_staff_id")
    Users assignedBy;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;
}
