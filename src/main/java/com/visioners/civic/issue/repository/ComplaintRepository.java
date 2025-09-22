package com.visioners.civic.issue.repository;

import com.visioners.civic.issue.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
}
