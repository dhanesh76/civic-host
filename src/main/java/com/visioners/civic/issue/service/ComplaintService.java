package com.visioners.civic.issue.service;

import com.visioners.civic.issue.dto.ComplaintRequestDto;
import com.visioners.civic.issue.entity.*;
import com.visioners.civic.issue.model.IssueSeverity;
import com.visioners.civic.issue.model.IssueStatus;
import com.visioners.civic.issue.repository.BlockRepository;
import com.visioners.civic.issue.repository.ComplaintRepository;
import com.visioners.civic.issue.repository.DepartmentRepository;
import com.visioners.civic.issue.repository.DistrictRepository;
import com.visioners.civic.user.repository.UserRepository;
import com.ticketmanagement.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final DistrictRepository districtRepository;
    private final BlockRepository blockRepository;
    private final DepartmentRepository departmentRepository;
    private final S3Service s3Service;

    @Transactional
    public Complaint createComplaint(ComplaintRequestDto dto, MultipartFile imageFile) throws IOException {

        Complaint complaint = new Complaint();
        complaint.setDescription(dto.getDescription());
        complaint.setCategory(dto.getCategory());
        complaint.setSubCategory(dto.getSubCategory());
        complaint.setLocation(dto.getLocation());

        // 1️⃣ Resolve District & Block from Location
        District district = districtRepository.findByName(dto.getLocation().getSubAdminArea())
                .orElseThrow(() -> new RuntimeException("District not found"));
        Block block = blockRepository.findByNameAndDistrict(dto.getLocation().getLocality(), district)
                .orElseThrow(() -> new RuntimeException("Block not found"));
        complaint.setDistrict(district);
        complaint.setBlock(block);

        // 2️⃣ Assign default severity
        complaint.setSeverity(getDefaultSeverity());

        // 3️⃣ S3 Image Upload
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = s3Service.uploadFile(imageFile, "complaints");
            complaint.setImageUrl(imageUrl);
        }

        // 4️⃣ Default status
        complaint.setStatus(IssueStatus.PENDING);

        // 5️⃣ Map raisedBy user
        complaint.setRaisedBy(userRepository.findById(dto.getRaisedById())
                .orElseThrow(() -> new RuntimeException("User not found")));

        // 6️⃣ Assign default department (MVP placeholder, later ML service)
        complaint.setDepartment(getDefaultDepartment(block));

        // 7️⃣ Timestamps
        complaint.setCreatedAt(Instant.now());
        complaint.setUpdatedAt(Instant.now());

        return complaintRepository.save(complaint);
    }

    // -----------------------
    // Default severity function
    private IssueSeverity getDefaultSeverity() {
        return IssueSeverity.MEDIUM;
    }

    // -----------------------
    // Default department function
    private Department getDefaultDepartment(Block block) {
        // Hardcoded for MVP, always returns 'ROAD' department for the block
        return departmentRepository.findByNameAndBlock("ROAD", block)
                .orElseThrow(() -> new RuntimeException("Default department not found"));
    }
}
