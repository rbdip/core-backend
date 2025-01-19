package ru.stepagin.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.stepagin.backend.entity.ProjectVersionEntity;

@Repository
public interface ProjectVersionRepository extends JpaRepository<ProjectVersionEntity, Long> {
    @Query("""
            select p from ProjectVersionEntity p
            where upper(p.projectCard.author.username) = upper(?1) and upper(p.projectCard.name) = upper(?2)
            and upper(p.versionName) = upper(?3)""")
    ProjectVersionEntity findProjectByVersion(String username, String projectName, String version);

    @Query("""
            select p from ProjectVersionEntity p
            where upper(p.projectCard.author.username) = upper(?1) and upper(p.projectCard.name) = upper(?2)
            order by p.displayOrder, p.createdOn DESC limit 1""")
    ProjectVersionEntity findProject(String username, String projectName);

    @Transactional
    @Modifying
    @Query("update ProjectVersionEntity p set p.isDeleted = true, p.deletedOn = CURRENT_TIMESTAMP, p.updatedOn = CURRENT_TIMESTAMP " +
            "where upper(p.projectCard.author.username) = upper(:author) " +
            "and upper(p.projectCard.name) = upper(:projectName)")
    void deleteAllByAuthorAndName(
            @Param("author") String author,
            @Param("projectName") String projectName
    );

    @Transactional
    @Modifying
    @Query("""
            update ProjectVersionEntity p set p.isDeleted = true, p.deletedOn = CURRENT_TIMESTAMP, p.updatedOn = CURRENT_TIMESTAMP
            where upper(p.projectCard.author.username) = upper(:author)
            and upper(p.projectCard.name) = upper(:projectName)
            and upper(p.versionName) = upper(:versionName)""")
    void deleteVersion(
            @Param("author") String author,
            @Param("projectName") String projectName,
            @Param("versionName") String versionName
    );


}
