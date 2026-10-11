package com.campuscomplaint.campuscomplaintbackend.controller;

import com.campuscomplaint.campuscomplaintbackend.entity.Complaint;
import com.campuscomplaint.campuscomplaintbackend.service.ComplaintService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // CREATE - Submit a complaint as the authenticated student
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Complaint createComplaint(
            @RequestBody Complaint complaint,
            Authentication authentication) {

        return complaintService.saveComplaintForUser(
                complaint,
                authentication.getName()
        );
    }

    // READ - Get complaints belonging to the authenticated role
    @GetMapping
    public List<Complaint> getAllComplaints(
            Authentication authentication) {

        return complaintService.getComplaintsForUser(
                authentication.getName()
        );
    }

    // READ - Get one complaint
    @GetMapping("/{id}")
    public Complaint getComplaintById(
            @PathVariable Long id,
            Authentication authentication) {

        return complaintService.getComplaintForUser(
                id,
                authentication.getName()
        );
    }

    // UPDATE - Maintenance can update complaint status only
    @PutMapping("/{id}/status")
    public Complaint updateComplaintStatus(
            @PathVariable Long id,
            @RequestBody Complaint updatedComplaint,
            Authentication authentication) {

        return complaintService.updateStatusForMaintenance(
                id,
                updatedComplaint.getStatus(),
                authentication.getName()
        );
    }

    // DELETE - Students can delete only their own complaints
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComplaint(
            @PathVariable Long id,
            Authentication authentication) {

        complaintService.deleteComplaintForUser(
                id,
                authentication.getName()
        );
    }
}
