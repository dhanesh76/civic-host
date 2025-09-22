package com.visioners.civic.issue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visioners.civic.issue.dto.ComplaintCreateDto;
import com.visioners.civic.issue.entity.Complaint;
import com.visioners.civic.issue.service.ComplaintService;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/complaints")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Add CORS support for frontend
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Complaint> createComplaint(
            @RequestParam("complaint") String complaintJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException, java.io.IOException {
        ComplaintCreateDto complaintDto = new ObjectMapper().readValue(complaintJson, ComplaintCreateDto.class);
        Complaint complaint = complaintService.createComplaint(complaintDto, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(complaint);
    }
}
