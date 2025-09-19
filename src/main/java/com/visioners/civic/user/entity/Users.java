package com.visioners.civic.user.entity;

import java.time.Instant;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.visioners.civic.role.entity.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data
public class Users {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    private String username;

    @Column(nullable = false, unique = true)
    private String mobileNumber;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch=FetchType.EAGER) 
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name="user_id"),
        inverseJoinColumns = @JoinColumn(name="role_id")
    )
    @JsonManagedReference
    Set<Role> roles;
    
    @Column(nullable = false)
    boolean isVerified;

    @CreationTimestamp
    Instant createdAt;
}
