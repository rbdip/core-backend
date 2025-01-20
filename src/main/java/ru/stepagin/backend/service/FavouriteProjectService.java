package ru.stepagin.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.stepagin.backend.entity.FavouriteProjectEntity;
import ru.stepagin.backend.entity.ProjectCardEntity;
import ru.stepagin.backend.entity.ProjectVersionEntity;
import ru.stepagin.backend.entity.UserEntity;
import ru.stepagin.backend.exception.ProjectNotFoundException;
import ru.stepagin.backend.exception.UserNotFoundException;
import ru.stepagin.backend.repository.FavouriteProjectRepository;

import java.util.List;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class FavouriteProjectService {
    private final FavouriteProjectRepository favouriteProjectRepository;
    private final ProjectService projectService;
    private final UserService userService;

    public List<ProjectCardEntity> getAllFavouriteProjects(
            String username
    ) {
        return favouriteProjectRepository.findAllByUsername(username).stream()
                .map(FavouriteProjectEntity::getProjectCard).
                toList();
    }

    public void createFavouriteProject(
            String actorName,
            String authorName,
            String projectName
    ) {
        ProjectVersionEntity project = projectService.getProject(authorName, projectName, null);
        if (project == null) {
            throw new ProjectNotFoundException(authorName, projectName);
        }
        UserEntity actor = userService.getByUsername(actorName);
        if (actor == null) {
            throw new UserNotFoundException(actorName);
        }
        FavouriteProjectEntity entity = favouriteProjectRepository.findByUsernameAndProjectCard(
                actorName,
                project.getProjectCard().getId()
        );
        if (entity != null) {
            return;
        }
        FavouriteProjectEntity newEntity = new FavouriteProjectEntity();
        newEntity.setUser(actor);
        newEntity.setProjectCard(project.getProjectCard());
        favouriteProjectRepository.save(newEntity);
    }

    public void deleteFavouriteProject(
            String actorName,
            String authorName,
            String projectName
    ) {
        ProjectVersionEntity project = projectService.getProject(authorName, projectName, null);
        if (project == null) {
            throw new ProjectNotFoundException(authorName, projectName);
        }
        UserEntity actor = userService.getByUsername(actorName);
        if (actor == null) {
            throw new UserNotFoundException(actorName);
        }
        FavouriteProjectEntity entity = favouriteProjectRepository.findByUsernameAndProjectCard(
                actorName,
                project.getProjectCard().getId()
        );
        if (entity == null) {
            return;
        }
        favouriteProjectRepository.delete(entity);
    }
}
