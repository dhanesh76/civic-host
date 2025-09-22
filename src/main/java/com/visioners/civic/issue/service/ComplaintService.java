package com.visioners.civic.issue.service;

import com.visioners.civic.aws.S3Service;
import com.visioners.civic.issue.dto.ComplaintCreateDto;
import com.visioners.civic.issue.entity.*;
import com.visioners.civic.issue.model.IssueSeverity;
import com.visioners.civic.issue.model.IssueStatus;
import com.visioners.civic.issue.repository.BlockRepository;
import com.visioners.civic.issue.repository.ComplaintRepository;
import com.visioners.civic.issue.repository.DepartmentRepository;
import com.visioners.civic.issue.repository.DistrictRepository;
import com.visioners.civic.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;

//import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UsersRepository userRepository;
    private final DistrictRepository districtRepository;
    private final BlockRepository blockRepository;
    private final DepartmentRepository departmentRepository;
    private final S3Service s3Service;

    @Transactional
    public Complaint createComplaint(ComplaintCreateDto dto, MultipartFile imageFile) throws IOException {

        Complaint complaint = new Complaint();
        complaint.setDescription(dto.description());
        complaint.setCategory(dto.category());
        complaint.setSubCategory(dto.subCategory());
        complaint.setLocation(dto.location());

        // 1️⃣ Resolve District & Block from Location
        District district = districtRepository.findByName(dto.location().getSubAdminArea())
                .orElseThrow(() -> new RuntimeException("District not found"));
        Block block = blockRepository.findByNameAndDistrict(dto.location().getLocality(), district)
                .orElseThrow(() -> new RuntimeException("Block not found"));
        complaint.setDistrict(district);
        complaint.setBlock(block);

        // 2️⃣ Assign default severity
        complaint.setSeverity(DefaultSeverity());

        // 3️⃣ S3 Image Upload
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = s3Service.uploadFile(imageFile, "complaints");
            complaint.setImageUrl(imageUrl);
        }

        // 4️⃣ Default status
        complaint.setStatus(IssueStatus.OPEN);

        // 5️⃣ Map raisedBy user
        complaint.setRaisedBy(userRepository.findById(dto.raisedById())
                .orElseThrow(() -> new RuntimeException("User not found")));

        // 6️⃣ Assign default department (MVP placeholder, later ML service)
        complaint.setDepartment(DefaultDepartment(block));

        // 7️⃣ Timestamps
        complaint.setCreatedAt(Instant.now());
        complaint.setUpdatedAt(Instant.now());

        return complaintRepository.save(complaint);
    }

    // -----------------------
    // Default severity function
    private IssueSeverity DefaultSeverity() {
        return IssueSeverity.MEDIUM;
    }

    // -----------------------
    // Default department function
    private Department DefaultDepartment(Block block) {
        // Hardcoded for MVP, always returns 'ROAD' department for the block
        return departmentRepository.findByNameAndBlock("ROAD", block)
                .orElseThrow(() -> new RuntimeException("Default department not found"));
    }
}
