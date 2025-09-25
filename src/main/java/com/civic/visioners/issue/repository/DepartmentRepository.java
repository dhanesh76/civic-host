package com.civic.visioners.issue.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visioners.civic.issue.entity.Block;
import com.visioners.civic.issue.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long>{

    Optional<Department> findByNameAndBlock(String string, Block block);
    
}
