package com.visioners.civic.role.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.visioners.civic.user.entity.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable=false, unique = true)
    String name;

    @ManyToMany(mappedBy = "roles")
    @JsonBackReference
    Set<Users> users;
}
