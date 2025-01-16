package ru.stepagin.backend.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.stepagin.backend.entity.ProjectCardEntity;
import ru.stepagin.backend.entity.ProjectVersionEntity;
import ru.stepagin.backend.entity.UserEntity;
import ru.stepagin.backend.repository.ProjectCardRepository;
import ru.stepagin.backend.repository.ProjectVersionRepository;
import ru.stepagin.backend.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectCardRepository projectCardRepository;
    private final UserRepository userRepository;
    private final ProjectVersionRepository projectVersionRepository;

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
}
