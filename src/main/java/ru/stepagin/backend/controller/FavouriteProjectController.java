package ru.stepagin.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.backend.dto.ProjectCardDtoResponse;
import ru.stepagin.backend.dto.ProjectCardWrapperDtoResponse;
import ru.stepagin.backend.entity.ProjectCardEntity;
import ru.stepagin.backend.mapper.ProjectMapper;
import ru.stepagin.backend.service.FavouriteProjectService;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("${app.path.start-prefix}")
@RequiredArgsConstructor
public class FavouriteProjectController {

    private final FavouriteProjectService favouriteProjectService;

    @GetMapping("/users/{username}/favourites")
    public ResponseEntity<ProjectCardWrapperDtoResponse> getFavouriteProjects(
            @PathVariable("username") String username
    ) {
        List<ProjectCardEntity> p = favouriteProjectService.getAllFavouriteProjects(username);
        return ResponseEntity.ok(new ProjectCardWrapperDtoResponse(
                        p.stream()
                                .map(ProjectMapper::toDto)
                                .toList()
                )
        );
    }

    @PutMapping("/projects/{author}/{name}/favourites")
    public ResponseEntity<Void> addFavouriteProject(
            @PathVariable(name = "author") String authorName,
            @PathVariable(name = "name") String projectName,
            Principal principal
    ) {
        String actorName = principal.getName();
        favouriteProjectService.createFavouriteProject(actorName, authorName, projectName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/projects/{author}/{name}/favourites")
    public ResponseEntity<ProjectCardDtoResponse> deleteFavouriteProject(
            @PathVariable(name = "author") String authorName,
            @PathVariable(name = "name") String projectName,
            Principal principal
    ) {
        String actorName = principal.getName();
        favouriteProjectService.deleteFavouriteProject(actorName, authorName, projectName);
        return ResponseEntity.noContent().build();
    }
}
