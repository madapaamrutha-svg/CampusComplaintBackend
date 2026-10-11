
package com.campuscomplaint.campuscomplaintbackend.repository;

import com.campuscomplaint.campuscomplaintbackend.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
}
