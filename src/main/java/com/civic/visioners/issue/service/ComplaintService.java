package com.civic.visioners.issue.service;

import com.visioners.civic.aws.S3Service;
import com.visioners.civic.issue.dto.ComplaintCreateDto;
import com.visioners.civic.issue.dto.ComplaintDto;
import com.visioners.civic.issue.entity.*;
import com.visioners.civic.issue.model.IssueStatus;
import com.visioners.civic.issue.repository.ComplaintRepository;
import com.visioners.civic.issue.repository.DepartmentRepository;
import com.visioners.civic.staff.entity.Staff;
import com.visioners.civic.staff.repository.StaffRepository;
import com.visioners.civic.user.entity.Users;
import com.visioners.civic.user.repository.UsersRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UsersRepository userRepository; // retained for other service methods
    private final S3Service s3Service;
    private final StaffRepository staffRepository;
    private final DepartmentRepository departmentRepository;

    
    @Transactional(readOnly = true)
    public List<Complaint> listComplaintsForOfficerDepartment(Users officerUser) {
        Staff officerStaff = staffRepository.findByUser(officerUser)
                .orElseThrow(() -> new RuntimeException("Officer is not mapped to staff/department"));
        return complaintRepository.findByDepartmentId(officerStaff.getDepartment().getId());
    }

    @Transactional(readOnly = true)
    public Page<Complaint> listComplaintsForOfficerDepartmentPaginated(
            Users officerUser, String status, Pageable pageable) {

        Staff officerStaff = staffRepository.findByUser(officerUser)
                .orElseThrow(() -> new RuntimeException("Officer is not mapped to staff/department"));

        if (status != null) {
            return complaintRepository.findByDepartmentIdAndStatus(
                    officerStaff.getDepartment().getId(), IssueStatus.valueOf(status), pageable);
        } else {
            return complaintRepository.findByDepartmentId(officerStaff.getDepartment().getId(), pageable);
        }
    }

    @Transactional
    public Complaint assignComplaintToStaff(Long complaintId, Users officerUser, Users staffUser) {
        Staff officerStaff = staffRepository.findByUser(officerUser)
                .orElseThrow(() -> new RuntimeException("Officer not mapped to staff/department"));

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (!complaint.getDepartment().getId().equals(officerStaff.getDepartment().getId())) {
            throw new RuntimeException("Officer cannot assign complaints outside their department");
        }

        Staff staff = staffRepository.findByUser(staffUser)
                .orElseThrow(() -> new RuntimeException("Staff user not mapped to staff entity"));

        if (!staff.getDepartment().getId().equals(officerStaff.getDepartment().getId())) {
            throw new RuntimeException("Staff is not in the same department as officer");
        }

        complaint.setAssignedStaff(staff.getUser());
        complaint.setAssignedBy(officerStaff.getUser());
        complaint.setStatus(IssueStatus.ASSIGNED);
        complaint.setUpdatedAt(Instant.now());

        return complaintRepository.save(complaint);
    }

    @Transactional(readOnly = true)
    public List<Complaint> listComplaintsByUser(Long userId, Optional<IssueStatus> status) {
        if (status.isPresent()) {
            return complaintRepository.findByRaisedByIdAndStatus(userId, status.get());
        } else {
            return complaintRepository.findByRaisedById(userId);
        }
    }

    public ComplaintDto mapToDto(Complaint complaint) {
        return new ComplaintDto(
                complaint.getId(),
                complaint.getImageUrl(),
                complaint.getDescription(),
                complaint.getCategory() != null ? complaint.getCategory().name() : null,
                complaint.getSubCategory() != null ? complaint.getSubCategory().name() : null,
                complaint.getSeverity(),
                complaint.getStatus(),
                complaint.getAssignedStaff() != null ? complaint.getAssignedStaff().getUsername() : null,
                complaint.getRaisedBy() != null ? complaint.getRaisedBy().getUsername() : null,
                complaint.getDepartment() != null ? complaint.getDepartment().getName() : null,
                complaint.getDistrict() != null ? complaint.getDistrict().getName() : null,
                complaint.getBlock() != null ? complaint.getBlock().getName() : null,
                complaint.getLocation(),
                complaint.getResolutionImageUrl()
        );   
    }
    // List complaints assigned to a specific staff
    public List<Complaint> listComplaintsAssignedToStaff(Users staff) {
        return complaintRepository.findByAssignedStaff(staff);
    }

// Update status by staff (start/resolve)
    @Transactional
    public Complaint updateComplaintStatusByStaff(Long complaintId, Users staff,
                                                IssueStatus newStatus,
                                                String remarks,
                                                String imageUrl) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (complaint.getAssignedStaff() == null || complaint.getAssignedStaff().getId() != staff.getId()) {
            throw new RuntimeException("Not authorized to update this complaint");
        }

        complaint.setStatus(newStatus);

        if (remarks != null) {
            complaint.setDescription(complaint.getDescription() + "\n\n[Remarks: " + remarks + "]");
        }
        if (imageUrl != null) {
            complaint.setImageUrl(imageUrl);
        }

        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint resolveComplaintByStaff(Long complaintId,
                                            Users staff,
                                            String remarks,
                                            MultipartFile imageFile) throws IOException {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (complaint.getAssignedStaff() == null || complaint.getAssignedStaff().getId() != staff.getId()) {
            throw new RuntimeException("Not authorized to update this complaint");
        }

        complaint.setStatus(IssueStatus.RESOLVED);

        // Append remarks if provided
        if (remarks != null && !remarks.isEmpty()) {
            String existingDescription = complaint.getDescription() != null ? complaint.getDescription() : "";
            complaint.setDescription(existingDescription + "\n\n[Remarks: " + remarks + "]");
        }

        // Upload file to S3 if present
        if (imageFile != null && !imageFile.isEmpty()) {
            String s3Url = s3Service.uploadFile(imageFile, complaintId.toString());
            complaint.setResolutionImageUrl(s3Url); // store the returned URL
        }

        complaint.setUpdatedAt(Instant.now());
        return complaintRepository.save(complaint);
    }


    @Transactional
    public Complaint approveComplaint(Long complaintId, Users officer) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (complaint.getStatus() != IssueStatus.RESOLVED) {
            throw new RuntimeException("Only resolved complaints can be approved");
        }

        complaint.setStatus(IssueStatus.APPROVED);
        complaint.setUpdatedAt(Instant.now());
        // Optionally, store who approved
        complaint.setApprovedBy(officer);
        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint rejectComplaint(Long complaintId, Users officer, String reason) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (complaint.getStatus() != IssueStatus.RESOLVED) {
            throw new RuntimeException("Only resolved complaints can be rejected");
        }

        complaint.setStatus(IssueStatus.REJECTED);
        complaint.setUpdatedAt(Instant.now());
        complaint.setResolutionNote(reason);
        // Optionally, store who rejected
        complaint.setApprovedBy(officer);
        return complaintRepository.save(complaint);
    }

    public Complaint createComplaint(ComplaintCreateDto dto, MultipartFile imageFile, Users raisedBy) throws IOException {
        Complaint complaint = new Complaint();
        complaint.setDescription(dto.getDescription());
        complaint.setCategory(dto.getCategory());
        complaint.setSubCategory(dto.getSubCategory());
        complaint.setLocation(dto.getLocation());
        complaint.setRaisedBy(raisedBy); // assign user directly

        // Determine department ID based on category
        Long deptId = 1L;
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        complaint.setDepartment(dept);
        

        // Save first to obtain an ID
        complaint = complaintRepository.save(complaint);

        // If an image was provided, upload and persist the URL
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = s3Service.uploadFile(imageFile, String.valueOf(complaint.getId()));
            complaint.setImageUrl(imageUrl);
            complaint = complaintRepository.save(complaint);
        }
        complaintRepository.save(complaint);

        return complaint;
    }
    
}
