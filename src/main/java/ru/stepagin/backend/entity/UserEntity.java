package ru.stepagin.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE account SET is_deleted = true, deleted_on = CURRENT_TIMESTAMP, updated_on = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "display_name")
    private String displayName;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "updated_on", nullable = false)
    private LocalDateTime updatedOn = LocalDateTime.now();

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_on")
    private LocalDateTime deletedOn;

    @Column(name = "last_login_on")
    private LocalDateTime lastLoginOn;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProjectCardEntity> projects = new HashSet<>();
}
