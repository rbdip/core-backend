package ru.stepagin.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.backend.dto.CreateProjectVersionDtoRequest;
import ru.stepagin.backend.dto.ProjectDetailsDtoResponse;
import ru.stepagin.backend.dto.ProjectVersionDto;
import ru.stepagin.backend.entity.ProjectVersionEntity;
import ru.stepagin.backend.mapper.ProjectMapper;
import ru.stepagin.backend.service.ProjectService;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("${app.path.start-prefix}/projects")
@RequiredArgsConstructor
public class ProjectVersionController {
    private final ProjectService projectService;

    @GetMapping("/{author}/{name}/versions")
    public ResponseEntity<List<ProjectVersionDto>> getProjectVersions(
            @PathVariable(name = "author") String author,
            @PathVariable(name = "name") String projectName
    ) {
        ProjectVersionEntity project = projectService.getProject(author, projectName, null);
        return ResponseEntity.ok(ProjectMapper.toProjectVersionDtos(project.getProjectCard().getProjectVersions()));
    }

    @PostMapping("/{author}/{name}/versions")
    public ResponseEntity<ProjectDetailsDtoResponse> createProjectVersion(
            @RequestBody CreateProjectVersionDtoRequest request,
            @PathVariable(name = "author") String author,
            @PathVariable(name = "name") String projectName,
            Principal principal
    ) {
        String actorName = principal.getName();
        if (!actorName.equals(author)) {
            throw new IllegalArgumentException("Can create versions only on your own projects");
        }
        ProjectVersionEntity project = projectService.createProjectVersion(request, author, projectName);
        return ResponseEntity.ok(ProjectMapper.toDto(project));
    }

//    @PatchMapping("/{author}/{name}/versions/{version}")
//    public ResponseEntity<ProjectDetailsDtoResponse> updateProjectVersion(
//            @RequestBody UpdateProjectVersionDtoRequest request,
//            @PathVariable(name = "author") String author,
//            @PathVariable(name = "name") String projectName
//    ) {
//        String username = principal.getName();
//
//    }
}
