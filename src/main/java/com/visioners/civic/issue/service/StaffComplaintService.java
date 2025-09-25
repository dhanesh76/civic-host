package com.visioners.civic.issue.service;


import com.visioners.civic.aws.S3Service;
import com.visioners.civic.issue.dto.ComplaintStatusUpdateDto;
import com.visioners.civic.issue.entity.Complaint;
import com.visioners.civic.issue.model.IssueStatus;
import com.visioners.civic.issue.repository.ComplaintRepository;
import com.visioners.civic.staff.entity.Staff;
import com.visioners.civic.staff.repository.StaffRepository;
import com.visioners.civic.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffComplaintService {

    private final ComplaintRepository complaintRepository;
    private final StaffRepository staffRepository;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public List<Complaint> listAssignedComplaints(Users staffUser) {
        Staff staff = staffRepository.findByUser(staffUser)
                .orElseThrow(() -> new RuntimeException("Staff not mapped to department"));
        return complaintRepository.findByAssignedStaffId(staffUser.getId());
    }

    @Transactional
    public Complaint updateComplaintStatus(Long complaintId, Users staffUser, ComplaintStatusUpdateDto dto, MultipartFile imageFile) throws IOException {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (complaint.getAssignedStaff() == null || complaint.getAssignedStaff().getId() != staffUser.getId()) {
            throw new RuntimeException("Complaint is not assigned to this staff");
        }

        // Update status & note
        complaint.setStatus(dto.getStatus());
        complaint.setResolutionNote(dto.getResolutionNote());

        // Upload image if exists
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = s3Service.uploadFile(imageFile, "complaint-resolutions");
            complaint.setResolutionImageUrl(imageUrl);
        }

        complaint.setUpdatedAt(Instant.now());
        return complaintRepository.save(complaint);
    }
}

