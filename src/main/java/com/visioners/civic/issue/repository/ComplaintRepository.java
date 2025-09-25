package com.visioners.civic.issue.repository;

import com.visioners.civic.issue.entity.Complaint;
import com.visioners.civic.issue.model.IssueStatus;
import com.visioners.civic.user.entity.Users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByDepartmentId(Long departmentId);

    List<Complaint> findByRaisedByIdAndStatus(Long userId, IssueStatus issueStatus);

    List<Complaint> findByRaisedById(Long userId);

    Page<Complaint> findByDepartmentIdAndStatus(long id, IssueStatus valueOf, Pageable pageable);

    Page<Complaint> findByDepartmentId(long id, Pageable pageable);

    List<Complaint> findByAssignedStaffId(long id);

    List<Complaint> findByAssignedStaff(Users staff);
}
