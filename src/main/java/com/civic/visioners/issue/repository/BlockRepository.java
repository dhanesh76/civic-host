package com.civic.visioners.issue.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.visioners.civic.issue.entity.Block;
import com.visioners.civic.issue.entity.District;
public interface BlockRepository extends JpaRepository<Block, Long>{

    Optional<Block> findByNameAndDistrict(String locality, District district);
    
}
