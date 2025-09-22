    package com.visioners.civic.staff.entity;

    import java.time.Instant;

    import org.hibernate.annotations.CreationTimestamp;

    import com.visioners.civic.issue.entity.Block;
    import com.visioners.civic.issue.entity.Department;
    import com.visioners.civic.user.entity.Users;

    import jakarta.persistence.Entity;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;
    import jakarta.persistence.JoinColumn;
    import jakarta.persistence.ManyToOne;
    import jakarta.persistence.OneToOne;

    @Entity
    public class Staff {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @OneToOne
        @JoinColumn(name = "user_id", nullable = false, unique = true)
        private Users user;

        @ManyToOne
        @JoinColumn(name = "block_id", nullable = false)
        private Block block;

        @ManyToOne
        @JoinColumn(name = "department_id", nullable = false)
        private Department department;

        @CreationTimestamp
        private Instant createdAt;
    }
