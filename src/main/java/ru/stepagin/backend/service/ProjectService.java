package ru.stepagin.backend.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.stepagin.backend.dto.CreateProjectDtoRequest;
import ru.stepagin.backend.dto.UpdateProjectDtoRequest;
import ru.stepagin.backend.entity.ProjectCardEntity;
import ru.stepagin.backend.entity.ProjectVersionEntity;
import ru.stepagin.backend.entity.UserEntity;
import ru.stepagin.backend.exception.EntityNotFoundException;
import ru.stepagin.backend.repository.ProjectCardRepository;
import ru.stepagin.backend.repository.ProjectVersionRepository;
import ru.stepagin.backend.repository.UserRepository;

import java.time.LocalDateTime;
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

    public ProjectVersionEntity getProject(String author, String projectName, String version) {
        // todo: assertions
        ProjectVersionEntity project = projectVersionRepository.findProjectByVersion(author, projectName, version);
        if (project == null) {
            project = projectVersionRepository.findProject(author, projectName);
        }
        if (project == null) {
            throw new EntityNotFoundException("Project " + author + "/" + projectName + " not found");
        }
        return project;
    }

    public ProjectVersionEntity createProject(
            @Valid CreateProjectDtoRequest createRequest,
            String username
    ) {
        UserEntity author = userRepository.findByUsername(username); // todo: получение из авторизации
        if (author == null) {
            throw new EntityNotFoundException("Username not found");
        }

        if (projectCardRepository.existsProjectByNameAndAuthor(createRequest.getName(), username)) {
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
            throw new EntityNotFoundException("Project not found");
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
        ProjectVersionEntity project;
        project = projectVersionRepository.findProject(author, projectName);
        if (project == null) {
            throw new EntityNotFoundException("Project not found");
        }
        if (version != null && !version.isEmpty()) {
            project = projectVersionRepository.findProjectByVersion(author, projectName, version);
        }
        if (project == null) {
            throw new EntityNotFoundException("Project version '" + version + "' not found");
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
}
