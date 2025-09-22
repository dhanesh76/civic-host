package com.visioners.civic.issue.controller;

import com.visioners.civic.issue.dto.ComplaintRequestDto;
import com.visioners.civic.issue.entity.Complaint;
import com.visioners.civic.issue.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<Complaint> createComplaint(
            @RequestPart("complaint") ComplaintCreateDto complaintDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            Complaint complaint = complaintService.createComplaint(complaintDto, imageFile);
            return ResponseEntity.ok(complaint);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
