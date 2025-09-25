package com.civic.visioners.staff.repository;

import com.visioners.civic.issue.entity.Complaint;
import com.visioners.civic.staff.entity.Staff;
import com.visioners.civic.user.entity.Users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    // find staff entry by user id
    Optional<Staff> findByUserId(Long userId);

    // find all staff in a department
    List<Staff> findByDepartmentId(Long departmentId);

    Optional<Staff> findByUser(Users officerUser);
}
