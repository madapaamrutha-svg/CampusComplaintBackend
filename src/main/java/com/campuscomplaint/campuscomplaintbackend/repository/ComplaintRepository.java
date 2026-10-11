package com.campuscomplaint.campuscomplaintbackend.repository;

import com.campuscomplaint.campuscomplaintbackend.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository
        extends JpaRepository<Complaint, Long> {

    List<Complaint> findByStudentId(Long studentId);
}
