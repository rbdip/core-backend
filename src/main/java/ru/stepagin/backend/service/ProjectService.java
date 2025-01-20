package ru.stepagin.backend.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.stepagin.backend.dto.CreateProjectDtoRequest;
import ru.stepagin.backend.dto.ProjectCardWrapperDtoResponse;
import ru.stepagin.backend.dto.UpdateProjectDtoRequest;
import ru.stepagin.backend.entity.ProjectCardEntity;
import ru.stepagin.backend.entity.UserEntity;
import ru.stepagin.backend.exception.ProjectAlreadyExistsException;
import ru.stepagin.backend.exception.ProjectNotFoundException;
import ru.stepagin.backend.exception.UserNotFoundException;
import ru.stepagin.backend.mapper.ProjectMapper;
import ru.stepagin.backend.repository.ProjectCardRepository;
import ru.stepagin.backend.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectCardRepository projectCardRepository;
    private final UserRepository userRepository;


    public ProjectCardWrapperDtoResponse getAllProjects(
            String query,
            PageRequest pageRequest
    ) {
        if (query == null || query.isEmpty()) {
            Page<ProjectCardEntity> page = projectCardRepository.findAll(pageRequest);
            return new ProjectCardWrapperDtoResponse(
                    page.getContent().stream().map(ProjectMapper::toCardDto).toList(),
                    page.getTotalElements(),
                    (long) page.getTotalPages()
            );
        }
        Page<ProjectCardEntity> page = projectCardRepository.findByQuery(query, pageRequest);

        return new ProjectCardWrapperDtoResponse(
                page.getContent().stream().map(ProjectMapper::toCardDto).toList(),
                page.getTotalElements(),
                (long) page.getTotalPages()
        );
    }

    public ProjectCardEntity getProject(
            String authorName,
            String projectName
    ) {
        UserEntity author = userRepository.findByUsername(authorName);
        if (author == null)
            throw new ProjectNotFoundException(authorName, projectName);

        ProjectCardEntity projectCard = projectCardRepository.findByNameAndAuthor(authorName, projectName);
        if (projectCard == null) {
            throw new ProjectNotFoundException(authorName, projectName);
        }

        return projectCard;
    }

    public ProjectCardEntity createProject(
            @Valid CreateProjectDtoRequest createRequest,
            String username
    ) {
        UserEntity author = userRepository.findByUsername(username);
        if (author == null) {
            throw new UserNotFoundException(username);
        }

        if (projectCardRepository.existsProjectByNameAndAuthor(username, createRequest.getName())) {
            throw new ProjectAlreadyExistsException(username, createRequest.getName());
        }

        ProjectCardEntity card = new ProjectCardEntity();
        card.setName(createRequest.getName());
        card.setTitle(createRequest.getTitle());
        card.setAuthor(author);
        card.setDescription(createRequest.getDescription());

        return projectCardRepository.save(card);
    }

    @Transactional
    public void deleteProject(String author, String projectName) {
        var project = projectCardRepository.findByNameAndAuthor(author, projectName);
        if (project == null) {
            throw new ProjectNotFoundException(author, projectName);
        }
        projectCardRepository.deleteAllByAuthorAndName(author, projectName);
    }

    @Transactional
    public ProjectCardEntity updateProjectData(
            @Valid UpdateProjectDtoRequest request,
            String author,
            String projectName
    ) {
        var card = projectCardRepository.findByNameAndAuthor(author, projectName);
        if (card == null) {
            throw new ProjectNotFoundException(author, projectName);
        }
        if (request.getTitle() == null
                && request.getName() == null
                && request.getDescription() == null) {
            throw new IllegalArgumentException("At least one parameter is required");
        }

        if (request.getTitle() != null) {
            card.setTitle(request.getTitle());
        }
        if (request.getName() != null) {
            card.setName(request.getName());
        }
        if (request.getDescription() != null) {
            card.setDescription(request.getDescription());
        }
        card.setUpdatedOn(LocalDateTime.now());
        return projectCardRepository.save(card);
    }

    @Transactional
    public void deleteAllProjectsByUser(UserEntity userToDelete) {
        projectCardRepository.deleteAllByAuthor(userToDelete.getUsername());
    }

}
