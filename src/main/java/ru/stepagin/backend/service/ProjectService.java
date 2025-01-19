package ru.stepagin.backend.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.stepagin.backend.dto.CreateProjectDtoRequest;
import ru.stepagin.backend.dto.CreateProjectVersionDtoRequest;
import ru.stepagin.backend.dto.UpdateProjectDtoRequest;
import ru.stepagin.backend.dto.UpdateProjectVersionDtoRequest;
import ru.stepagin.backend.entity.ProjectCardEntity;
import ru.stepagin.backend.entity.ProjectVersionEntity;
import ru.stepagin.backend.entity.UserEntity;
import ru.stepagin.backend.exception.ProjectAlreadyExistsException;
import ru.stepagin.backend.exception.ProjectNotFoundException;
import ru.stepagin.backend.exception.UserNotFoundException;
import ru.stepagin.backend.repository.ProjectCardRepository;
import ru.stepagin.backend.repository.ProjectVersionRepository;
import ru.stepagin.backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectCardRepository projectCardRepository;
    private final UserRepository userRepository;
    private final ProjectVersionRepository projectVersionRepository;

    private static ProjectVersionEntity createProjectVersionEntity(
            @Valid CreateProjectDtoRequest createRequest,
            ProjectCardEntity card
    ) {
        ProjectVersionEntity project = new ProjectVersionEntity();
        project.setProjectCard(card);
        if (createRequest.getVersionName() != null && !createRequest.getVersionName().isEmpty())
            project.setVersionName(createRequest.getVersionName());
        else
            project.setVersionName("default");
        project.setDescription(createRequest.getDescription());
        if (createRequest.getDisplayOrder() != null)
            project.setDisplayOrder(createRequest.getDisplayOrder());
        else
            project.setDisplayOrder(1);
        return project;
    }

    public List<ProjectCardEntity> getAllProjects() {
        return projectCardRepository.findAll();
    }

    public ProjectVersionEntity getProject(
            String author,
            String projectName,
            String version
    ) {
        UserEntity user = userRepository.findByUsername(author);
        if (user == null)
            throw new ProjectNotFoundException(author, projectName);

        ProjectCardEntity projectCard = projectCardRepository.findByNameAndAuthor(author, projectName);
        if (projectCard == null) {
            throw new ProjectNotFoundException(author, projectName);
        }

        ProjectVersionEntity projectVersion;
        if (version != null) {
            projectVersion = projectVersionRepository.findProjectByVersion(author, projectName, version);
            if (projectVersion != null) {
                return projectVersion;
            }
        }

        projectVersion = projectCard.getProjectVersions().stream()
                .min(Comparator.comparing(ProjectVersionEntity::getDisplayOrder)
                        .thenComparing(ProjectVersionEntity::getCreatedOn))
                .orElseThrow(() -> new ProjectNotFoundException(author, projectName));

        return projectVersion;
    }

    public ProjectVersionEntity createProject(
            @Valid CreateProjectDtoRequest createRequest,
            String username
    ) {
        UserEntity author = userRepository.findByUsername(username); // todo: получение из авторизации
        if (author == null) {
            throw new UserNotFoundException(username);
        }

        if (projectCardRepository.existsProjectByNameAndAuthor(username, createRequest.getName())) {
            throw new IllegalArgumentException("Project already exists");
        }

        ProjectCardEntity card = new ProjectCardEntity();
        card.setName(createRequest.getName());
        card.setTitle(createRequest.getTitle());
        card.setAuthor(author);

        ProjectVersionEntity project = createProjectVersionEntity(createRequest, card);
        card.addProjectVersion(project);

        return projectVersionRepository.save(project);
    }

    @Transactional
    public void deleteProject(String author, String projectName) {
        ProjectVersionEntity project = projectVersionRepository.findProject(author, projectName);
        if (project == null) {
            throw new ProjectNotFoundException(author, projectName);
        }
        projectVersionRepository.deleteAllByAuthorAndName(author, projectName);
        projectCardRepository.deleteAllByAuthorAndName(author, projectName);
    }

    @Transactional
    public ProjectVersionEntity updateProjectData(
            @Valid UpdateProjectDtoRequest request,
            String author,
            String projectName,
            String version
    ) {
        ProjectVersionEntity project = projectVersionRepository.findProject(author, projectName);
        if (project == null) {
            throw new ProjectNotFoundException(author, projectName);
        }
        if (version != null && !version.isEmpty()) {
            project = projectVersionRepository.findProjectByVersion(author, projectName, version);
        }
        if (project == null) {
            throw new ProjectNotFoundException(author, projectName, version);
        }
        if (request.getTitle() == null
                && request.getName() == null
                && request.getVersionName() == null
                && request.getDescription() == null) {
            throw new IllegalArgumentException("At least one parameter is required");
        }

        if (request.getTitle() != null) {
            project.getProjectCard().setTitle(request.getTitle());
        }
        if (request.getName() != null) {
            project.getProjectCard().setName(request.getName());
        }
        if (request.getVersionName() != null) {
            project.setVersionName(request.getVersionName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        project.setUpdatedOn(LocalDateTime.now());
        return projectVersionRepository.save(project);
    }

    @Transactional
    public void deleteAllProjectsByUser(UserEntity userToDelete) {
        for (ProjectCardEntity project : userToDelete.getProjects()) {
            projectVersionRepository.deleteAllByAuthorAndName(userToDelete.getUsername(), project.getName());
        }
        projectCardRepository.deleteAllByAuthor(userToDelete.getUsername());
    }

    public ProjectVersionEntity createProjectVersion(
            @Valid CreateProjectVersionDtoRequest request,
            String author,
            String projectName
    ) {
        ProjectCardEntity project = projectCardRepository.findByNameAndAuthor(author, projectName);
        if (project == null) {
            throw new ProjectNotFoundException(author, projectName);
        }
//        if (project.getAuthor() == null) todo посмотреть при удалении пользователя
//            throw new ProjectNotFoundException(author, projectName);
        if (project.getProjectVersions().stream()
                .map(ProjectVersionEntity::getVersionName)
                .anyMatch(request.getVersionName()::equalsIgnoreCase)
        ) {
            throw new ProjectAlreadyExistsException(author, projectName, request.getVersionName());
        }

        ProjectVersionEntity version = new ProjectVersionEntity();
        version.setVersionName(request.getVersionName());
        version.setDisplayOrder(request.getDisplayOrder());
        version.setDescription(request.getDescription());
        project.addProjectVersion(version);

        return projectVersionRepository.save(version);
    }

    @Transactional
    public ProjectVersionEntity updateProjectVersion(
            @Valid UpdateProjectVersionDtoRequest request,
            String author, // todo: чекнуть на null, добавить NotNull
            String projectName,
            String version
    ) {
        if (version == null || version.isEmpty()) {
            throw new IllegalArgumentException("Project version must be clarified");
        }

        if (request.getVersionName() == null
                && request.getDescription() == null
                && request.getDisplayOrder() == null) {
            throw new IllegalArgumentException("At least one parameter is required");
        }

        ProjectVersionEntity project = projectVersionRepository.findProjectByVersion(author, projectName, version);
        if (project == null) {
            throw new ProjectNotFoundException(author, projectName, version);
        }

        if (request.getVersionName() != null) {
            project.setVersionName(request.getVersionName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getDisplayOrder() != null) {
            project.setDisplayOrder(request.getDisplayOrder());
        }

        project.setUpdatedOn(LocalDateTime.now());
        return projectVersionRepository.save(project);
    }

    public void deleteProjectVersion(String author, String projectName, String versionName) {
        ProjectVersionEntity project = projectVersionRepository.findProjectByVersion(author, projectName, versionName);
        if (project == null) {
            throw new ProjectNotFoundException(author, projectName, versionName);
        }

        projectVersionRepository.deleteVersion(
                project.getProjectCard().getAuthor().getUsername(),
                project.getProjectCard().getName(),
                project.getVersionName()
        );
    }
}
