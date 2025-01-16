package ru.stepagin.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.stepagin.backend.entity.ProjectCardEntity;

@Repository
public interface ProjectCardRepository extends JpaRepository<ProjectCardEntity, Long> {
}
