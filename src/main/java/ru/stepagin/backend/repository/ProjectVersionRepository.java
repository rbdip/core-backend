package ru.stepagin.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.stepagin.backend.entity.ProjectVersionEntity;

@Repository
public interface ProjectVersionRepository extends JpaRepository<ProjectVersionEntity, Long> {
}
