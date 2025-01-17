package ru.stepagin.backend.service;

import jakarta.annotation.PostConstruct;
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
import ru.stepagin.backend.repository.ProjectCardRepository;
import ru.stepagin.backend.repository.ProjectVersionRepository;
import ru.stepagin.backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectCardRepository projectCardRepository;
    private final UserRepository userRepository;
    private final ProjectVersionRepository projectVersionRepository;

    private static ProjectVersionEntity createProjectVersionEntity(
            @Validated CreateProjectDtoRequest createRequest,
            ProjectCardEntity card
    ) {
        ProjectVersionEntity project = new ProjectVersionEntity();
        project.setProjectCard(card);
        if (createRequest.getDisplayVersion() != null && !createRequest.getDisplayVersion().isEmpty())
            project.setDisplayName(createRequest.getDisplayVersion());
        else
            project.setDisplayName("default");
        project.setDescription(createRequest.getDescription());
        if (createRequest.getDisplayOrder() != null)
            project.setDisplayOrder(createRequest.getDisplayOrder());
        else
            project.setDisplayOrder(1);
        return project;
    }

    @PostConstruct
    public void init() {
//        var user = createUserEntity("Stupapupa1");
//        var card = createProjectCard("stupa-project", "Микросервис типа", user);
//        var version1 = createProjectVersion("Default", 2, card);
//        var version2 = createProjectVersion("v0.0.1-SNAPSHOT", 1, card);
    }

    private UserEntity createUserEntity(String username) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(username);
        user.setDisplayName(username);
        return userRepository.save(user);
    }

    private ProjectVersionEntity createProjectVersion(String displayName, int displayOrder, ProjectCardEntity card) {
        ProjectVersionEntity projectVersion = new ProjectVersionEntity();
        projectVersion.setDisplayName(displayName);
        projectVersion.setDisplayOrder(displayOrder);
        projectVersion.setProjectCard(card);
        return projectVersionRepository.save(projectVersion);
    }

    private ProjectCardEntity createProjectCard(String name, String title, UserEntity user) {
        ProjectCardEntity projectCard = new ProjectCardEntity();
        projectCard.setName(name);
        projectCard.setTitle(title);
        projectCard.setAuthor(user);
        return projectCardRepository.save(projectCard);
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
        return project;
    }

    public ProjectVersionEntity createProject(
            @Validated CreateProjectDtoRequest createRequest,
            String username
    ) {
        UserEntity author = userRepository.findByUsername(username); // todo: получение из авторизации
        if (author == null) {
            throw new IllegalArgumentException("Username not found");
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
        projectVersionRepository.deleteAllByAuthorAndName(author, projectName);
        projectCardRepository.deleteAllByAuthorAndName(author, projectName);
    }

    @Transactional
    public ProjectVersionEntity updateProjectData(
            @Validated UpdateProjectDtoRequest request,
            String author,
            String projectName,
            String version
    ) {
        ProjectVersionEntity project;
        project = projectVersionRepository.findProject(author, projectName);
        if (project == null) {
            throw new IllegalArgumentException("Project not found");
        }
        if (version != null && !version.isEmpty()) {
            project = projectVersionRepository.findProjectByVersion(author, projectName, version);
        }
        if (project == null) {
            throw new IllegalArgumentException("Project version " + version + " not found");
        }
        if (request.getTitle() == null
                && request.getProjectName() == null
                && request.getVersionName() == null
                && request.getDescription() == null) {
            throw new IllegalArgumentException("At least one parameter required");
        }

        if (request.getTitle() != null) {
            project.getProjectCard().setTitle(request.getTitle());
        }
        if (request.getProjectName() != null) {
            project.getProjectCard().setName(request.getProjectName());
        }
        if (request.getVersionName() != null) {
            project.setDisplayName(request.getVersionName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        project.setUpdatedOn(LocalDateTime.now());
        return projectVersionRepository.save(project);
    }
}
