package com.visioners.civic.issue.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visioners.civic.issue.entity.District;

public interface DistrictRepository extends JpaRepository<District, Long>{

    Optional<District> findByName(String subAdminArea);
    
}
