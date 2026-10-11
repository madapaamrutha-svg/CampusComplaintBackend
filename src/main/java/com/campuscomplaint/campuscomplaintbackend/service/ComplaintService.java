package com.campuscomplaint.campuscomplaintbackend.service;

import com.campuscomplaint.campuscomplaintbackend.entity.Complaint;
import com.campuscomplaint.campuscomplaintbackend.repository.ComplaintRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository repository;

    public ComplaintService(ComplaintRepository repository) {
        this.repository = repository;
    }

    public Complaint saveComplaint(Complaint complaint) {
        return repository.save(complaint);
    }

    public List<Complaint> getAllComplaints() {
        return repository.findAll();
    }

    public Complaint getComplaintById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Complaint not found with id: " + id));
    }

    public void deleteComplaint(Long id) {
        repository.deleteById(id);
    }
}