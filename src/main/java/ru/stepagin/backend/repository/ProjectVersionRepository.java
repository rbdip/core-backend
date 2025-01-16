package ru.stepagin.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.stepagin.backend.entity.ProjectVersionEntity;

@Repository
public interface ProjectVersionRepository extends JpaRepository<ProjectVersionEntity, Long> {
    @Query("""
            select p from ProjectVersionEntity p
            where upper(p.projectCard.author.username) = upper(?1) and upper(p.projectCard.name) = upper(?2) and p.displayName = ?3""")
    ProjectVersionEntity findProjectByVersion(String username, String projectName, String version);

    @Query("""
            select p from ProjectVersionEntity p
            where upper(p.projectCard.author.username) = upper(?1) and upper(p.projectCard.name) = upper(?2)
            order by p.displayOrder, p.createdOn DESC limit 1""")
    ProjectVersionEntity findProject(String username, String projectName);


}
