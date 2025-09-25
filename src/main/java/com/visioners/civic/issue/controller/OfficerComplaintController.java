package com.visioners.civic.issue.controller;

import com.visioners.civic.issue.dto.AssignComplaintDto;
import com.visioners.civic.issue.dto.ComplaintDto;
import com.visioners.civic.issue.entity.Complaint;
import com.visioners.civic.issue.service.ComplaintService;
import com.visioners.civic.user.entity.Users;
import com.visioners.civic.user.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/officer/complaints")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OfficerComplaintController {

    private final ComplaintService complaintService;
    private final UsersRepository usersRepository;
    private Users getCurrentUser(Authentication authentication){
        String mobile = authentication.getName();
        return usersRepository.findByMobileNumberWithRoles(mobile)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    @GetMapping
    public ResponseEntity<List<ComplaintDto>> listDepartmentComplaints(Authentication authentication){
        Users officer = getCurrentUser(authentication);
        List<Complaint> complaints = complaintService.listComplaintsForOfficerDepartment(officer);
        List<ComplaintDto> dtoList = complaints.stream()
                .map(complaintService::mapToDto)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/home")
    public ResponseEntity<Page<ComplaintDto>> officerHomeFeed(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        Users officer = getCurrentUser(authentication);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Complaint> complaints = complaintService
                .listComplaintsForOfficerDepartmentPaginated(officer, status, pageable);

        Page<ComplaintDto> dtoPage = complaints.map(complaintService::mapToDto);
        return ResponseEntity.ok(dtoPage);
    }


    @PostMapping("/{complaintId}/assign")
    public ResponseEntity<Complaint> assignToStaff(
            @PathVariable Long complaintId,
            @RequestBody AssignComplaintDto dto,
            Authentication authentication){
        Users officer = getCurrentUser(authentication);
        Users staffUser = usersRepository.findById(dto.staffUserId())
                .orElseThrow(() -> new RuntimeException("Staff user not found"));
        Complaint updated = complaintService.assignComplaintToStaff(complaintId, officer, staffUser);
        return ResponseEntity.ok(updated);
    }
}
