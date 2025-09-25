package com.civic.visioners.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.visioners.civic.user.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    @Query("SELECT DISTINCT u FROM Users u LEFT JOIN FETCH u.roles WHERE u.mobileNumber = :mobileNumber")
    Optional<Users> findByMobileNumberWithRoles(@Param("mobileNumber") String mobileNumber);

    boolean existsByMobileNumber(String mobileNumber);
}

