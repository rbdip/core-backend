package ru.stepagin.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.stepagin.backend.entity.ProjectCardEntity;

@Repository
public interface ProjectCardRepository extends JpaRepository<ProjectCardEntity, Long> {
    @Query("""
            select p from ProjectCardEntity p
            where upper(p.name) = upper(:name) and upper(p.author.username) = upper(:username)""")
    ProjectCardEntity findByNameAndAuthor(@Param("name") String name, @Param("username") String username);

    @Query("""
            select (count(p) > 0) from ProjectCardEntity p
            where upper(p.name) = upper(:name) and upper(p.author.username) = upper(:username)""")
    boolean existsProjectByNameAndAuthor(@Param("name") String name, @Param("username") String username);

    @Transactional
    @Modifying
    @Query("update ProjectCardEntity p set p.isDeleted = true, p.deletedOn = CURRENT_TIMESTAMP, p.updatedOn = CURRENT_TIMESTAMP " +
            "where upper(p.author.username) = upper(:username) and upper(p.name) = upper(:name)")
    void deleteAllByAuthorAndName(String username, String name);

    @Transactional
    @Modifying
    @Query("update ProjectCardEntity p set p.isDeleted = true, p.deletedOn = CURRENT_TIMESTAMP, p.updatedOn = CURRENT_TIMESTAMP " +
            "where upper(p.author.username) = upper(:username)")
    void deleteAllByAuthor(String username);


}
