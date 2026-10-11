
package com.campuscomplaint.campuscomplaintbackend.controller;

import com.campuscomplaint.campuscomplaintbackend.entity.Complaint;
import com.campuscomplaint.campuscomplaintbackend.service.ComplaintService;
import org.springframework.http.HttpStatus;
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

    // CREATE - Submit a new complaint
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Complaint createComplaint(
            @RequestBody Complaint complaint) {
        return complaintService.saveComplaint(complaint);
    }

    // READ - Get all complaints
    @GetMapping
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    // READ - Get one complaint by ID
    @GetMapping("/{id}")
    public Complaint getComplaintById(
            @PathVariable Long id) {
        return complaintService.getComplaintById(id);
    }

    // UPDATE - Update complaint details and status
    @PutMapping("/{id}")
    public Complaint updateComplaint(
            @PathVariable Long id,
            @RequestBody Complaint updatedComplaint) {

        Complaint existing =
                complaintService.getComplaintById(id);

        existing.setTitle(updatedComplaint.getTitle());
        existing.setCategory(updatedComplaint.getCategory());
        existing.setLocation(updatedComplaint.getLocation());
        existing.setDescription(updatedComplaint.getDescription());
        existing.setStatus(updatedComplaint.getStatus());

        return complaintService.saveComplaint(existing);
    }

    // DELETE - Delete a complaint by ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
    }
}
