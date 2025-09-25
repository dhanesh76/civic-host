package com.civic.visioners.issue.controller;


import com.visioners.civic.issue.dto.ComplaintDto;
import com.visioners.civic.issue.dto.UpdateComplaintStatusDto;
import com.visioners.civic.issue.entity.Complaint;
import com.visioners.civic.issue.model.IssueStatus;
import com.visioners.civic.issue.service.ComplaintService;
import com.visioners.civic.user.entity.Users;
import com.visioners.civic.user.repository.UsersRepository;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/fieldworker/complaints")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FieldWorkerComplaintController {

    private final ComplaintService complaintService;
    private final UsersRepository usersRepository;

    private Users getCurrentUser(Authentication authentication) {
        String mobile = authentication.getName();
        return usersRepository.findByMobileNumberWithRoles(mobile)
                .orElseThrow(() -> new RuntimeException("Fieldworker not found"));
    }

    // 1️⃣ List all complaints assigned to fieldworker
    @GetMapping
    public ResponseEntity<List<ComplaintDto>> listAssignedComplaints(Authentication authentication) {
        Users fieldworker = getCurrentUser(authentication);
        List<Complaint> complaints = complaintService.listComplaintsAssignedToStaff(fieldworker);
        List<ComplaintDto> dtoList = complaints.stream()
                .map(complaintService::mapToDto)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    // 2️⃣ Mark complaint as IN_PROGRESS
    @PutMapping("/{complaintId}/start")
    public ResponseEntity<ComplaintDto> startWork(
            @PathVariable Long complaintId,
            Authentication authentication) {
        Users fieldworker = getCurrentUser(authentication);
        Complaint complaint = complaintService.updateComplaintStatusByStaff(
                complaintId, fieldworker, IssueStatus.ASSIGNED, null, null
        );
        return ResponseEntity.ok(complaintService.mapToDto(complaint));
    }

    @PutMapping("/{complaintId}/resolve")
    public ResponseEntity<ComplaintDto> resolveWork(
            @PathVariable Long complaintId,
            @RequestBody UpdateComplaintStatusDto dto,
            Authentication authentication) {
        Users fieldworker = getCurrentUser(authentication);
        Complaint complaint = complaintService.updateComplaintStatusByStaff(
                complaintId, fieldworker, IssueStatus.RESOLVED, dto.remarks(), dto.imageUrl()
        );
        return ResponseEntity.ok(complaintService.mapToDto(complaint));
    }

    @PutMapping(value = "/{complaintId}/resolve", consumes = {"multipart/form-data"})
    public ResponseEntity<ComplaintDto> resolveWork(
            @PathVariable Long complaintId,
            @RequestParam("remarks") String remarks,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            Authentication authentication) throws IOException, java.io.IOException {
    
        Users fieldworker = getCurrentUser(authentication);
    
        Complaint complaint = complaintService.resolveComplaintByStaff(
                complaintId,
                fieldworker,
                remarks,
                imageFile
        );
    
        return ResponseEntity.ok(complaintService.mapToDto(complaint));
    }

        @PutMapping("/{complaintId}/approve")
        public ResponseEntity<Complaint> approveComplaint(
                @PathVariable Long complaintId,
                Authentication authentication) {
        Users officer = getCurrentUser(authentication);
        Complaint complaint = complaintService.approveComplaint(complaintId, officer);
        return ResponseEntity.ok(complaint);
        }

        @PutMapping("/{complaintId}/reject")
        public ResponseEntity<Complaint> rejectComplaint(
                @PathVariable Long complaintId,
                @RequestParam String reason,
                Authentication authentication) {
        Users officer = getCurrentUser(authentication);
        Complaint complaint = complaintService.rejectComplaint(complaintId, officer, reason);
        return ResponseEntity.ok(complaint);
}

}       

