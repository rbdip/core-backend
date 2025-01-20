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
@Table(
        name = "project_card",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"account_id", "name", "deleted_on"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE project_card SET is_deleted = true, deleted_on = CURRENT_TIMESTAMP, updated_on = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class ProjectCardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    private UserEntity author;

    @Column(nullable = false)
    private String name;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "updated_on", nullable = false)
    private LocalDateTime updatedOn = LocalDateTime.now();

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_on")
    private LocalDateTime deletedOn;

    @OneToMany(mappedBy = "projectCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectVersionEntity> projectVersions = new HashSet<>();

    @OneToMany(mappedBy = "projectCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FavouriteProjectEntity> projectLikes = new HashSet<>();

    public void addProjectVersion(ProjectVersionEntity projectVersion) {
        if (projectVersions.contains(projectVersion) || projectVersion == null) {
            return;
        }
        projectVersion.setProjectCard(this);
        projectVersions.add(projectVersion);
    }

    public void removeProjectVersion(ProjectVersionEntity projectVersion) {
        if (!projectVersions.contains(projectVersion) || projectVersion == null) {
            return;
        }
        projectVersions.remove(projectVersion);
    }
}
