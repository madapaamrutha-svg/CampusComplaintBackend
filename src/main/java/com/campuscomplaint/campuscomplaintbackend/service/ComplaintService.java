package com.campuscomplaint.campuscomplaintbackend.service;

import com.campuscomplaint.campuscomplaintbackend.entity.Complaint;
import com.campuscomplaint.campuscomplaintbackend.entity.User;
import com.campuscomplaint.campuscomplaintbackend.repository.ComplaintRepository;
import com.campuscomplaint.campuscomplaintbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository repository;
    private final UserRepository userRepository;

    public ComplaintService(
            ComplaintRepository repository,
            UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    // CREATE: Submit a complaint using student ID
    @Transactional
    public Complaint saveComplaint(
            Complaint complaint, Long studentId) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student account not found."));

        if (student.getRole() != User.Role.STUDENT) {
            throw new IllegalArgumentException(
                    "Only students can submit complaints.");
        }

        complaint.setId(null);
        complaint.setStudent(student);
        complaint.setStatus("Pending");

        return repository.save(complaint);
    }

    // CREATE: Submit a complaint for authenticated student
    @Transactional
    public Complaint saveComplaintForUser(
            Complaint complaint, String email) {

        User student = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Student account not found."));

        if (student.getRole() != User.Role.STUDENT) {
            throw new IllegalArgumentException(
                    "Only students can submit complaints.");
        }

        complaint.setId(null);
        complaint.setStudent(student);
        complaint.setStatus("Pending");

        return repository.save(complaint);
    }

    // READ: Get all complaints
    public List<Complaint> getAllComplaints() {
        return repository.findAll();
    }

    // READ: Get complaints by student ID
    public List<Complaint> getStudentComplaints(Long studentId) {
        return repository.findByStudentId(studentId);
    }

    // READ: Get complaints for a user by email
    public List<Complaint> getComplaintsForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User account not found."));

        if (user.getRole() == User.Role.MAINTENANCE) {
            return repository.findAll();
        }

        return repository.findByStudentId(user.getId());
    }

    // READ: Get one complaint by ID
    public Complaint getComplaintById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Complaint not found with id: " + id));
    }

    // READ: Get one complaint only if it belongs to the user
    public Complaint getComplaintForUser(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User account not found."));

        Complaint complaint = getComplaintById(id);

        if (user.getRole() == User.Role.STUDENT
                && (complaint.getStudent() == null
                || !complaint.getStudent().getId().equals(user.getId()))) {
            throw new IllegalArgumentException(
                    "You can only view your own complaints.");
        }

        return complaint;
    }

    // UPDATE: Update existing complaint details
    @Transactional
    public Complaint updateComplaint(
            Long id, Complaint updatedComplaint) {

        Complaint existing = getComplaintById(id);

        if (updatedComplaint.getTitle() != null) {
            existing.setTitle(updatedComplaint.getTitle());
        }

        if (updatedComplaint.getCategory() != null) {
            existing.setCategory(updatedComplaint.getCategory());
        }

        if (updatedComplaint.getLocation() != null) {
            existing.setLocation(updatedComplaint.getLocation());
        }

        if (updatedComplaint.getDescription() != null) {
            existing.setDescription(updatedComplaint.getDescription());
        }

        if (updatedComplaint.getStatus() != null) {
            existing.setStatus(validateStatus(
                    updatedComplaint.getStatus()));
        }

        return repository.save(existing);
    }

    // UPDATE: Maintenance status update
    @Transactional
    public Complaint updateStatusForMaintenance(
            Long id, String status, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User account not found."));

        if (user.getRole() != User.Role.MAINTENANCE) {
            throw new IllegalArgumentException(
                    "Only maintenance users can update complaint status.");
        }

        Complaint complaint = getComplaintById(id);
        complaint.setStatus(validateStatus(status));

        return repository.save(complaint);
    }

    private String validateStatus(String status) {
        String value = status == null ? "" : status.trim();

        if (!value.equals("Pending")
                && !value.equals("In Progress")
                && !value.equals("Resolved")) {
            throw new IllegalArgumentException(
                    "Invalid complaint status.");
        }

        return value;
    }

    // DELETE: Delete only the authenticated user's complaint
    @Transactional
    public void deleteComplaintForUser(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User account not found."));

        if (user.getRole() != User.Role.STUDENT) {
            throw new IllegalArgumentException(
                    "Only students can delete their complaints.");
        }

        Complaint complaint = getComplaintById(id);

        if (complaint.getStudent() == null
                || !complaint.getStudent().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "You can only delete your own complaints.");
        }

        repository.delete(complaint);
    }

    // DELETE: Legacy method
    @Transactional
    public void deleteComplaint(Long id, Long studentId) {
        Complaint complaint = getComplaintById(id);

        if (complaint.getStudent() == null
                || !complaint.getStudent().getId().equals(studentId)) {
            throw new IllegalArgumentException(
                    "You can only delete your own complaints.");
        }

        repository.delete(complaint);
    }
}
