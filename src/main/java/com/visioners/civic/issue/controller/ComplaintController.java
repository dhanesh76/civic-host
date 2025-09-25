package com.visioners.civic.issue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visioners.civic.issue.dto.ComplaintCreateDto;
import com.visioners.civic.issue.entity.Complaint;
import com.visioners.civic.issue.model.IssueStatus;
import com.visioners.civic.issue.service.ComplaintService;
import com.visioners.civic.user.entity.Users;
import com.visioners.civic.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/complaints")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final UsersRepository userRepository;
        

    private Users getCurrentUser(Authentication authentication) {
        String mobile = authentication.getName();
        return userRepository.findByMobileNumberWithRoles(mobile)
                .orElseThrow(() -> new RuntimeException("Fieldworker not found"));
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Complaint> createComplaint(
            Authentication authentication,
            @RequestParam("complaint") String complaintJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {
        Users currentUser = getCurrentUser(authentication);

        // Deserialize JSON into DTO
        ComplaintCreateDto complaintDto = new ObjectMapper().readValue(complaintJson, ComplaintCreateDto.class);

        // Pass both dto + currentUser to service
        Complaint complaint = complaintService.createComplaint(complaintDto, imageFile, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(complaint);
    }


    @GetMapping
    public ResponseEntity<List<Complaint>> getMyComplaints(
            @RequestParam(value = "status", required = false) IssueStatus status,
            Authentication authentication
    ) {
        Users user = userRepository.findByMobileNumberWithRoles(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Complaint> complaints = complaintService.listComplaintsByUser(user.getId(), Optional.ofNullable(status));
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/home")
    public ResponseEntity<Page<Complaint>> officerHomeFeed(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        Users officer = userRepository.findByMobileNumberWithRoles(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Complaint> complaints = complaintService.listComplaintsForOfficerDepartmentPaginated(officer, status, pageable);
        return ResponseEntity.ok(complaints);
    }
}
