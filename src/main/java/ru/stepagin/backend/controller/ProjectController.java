package ru.stepagin.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.backend.dto.CreateProjectDtoRequest;
import ru.stepagin.backend.dto.ProjectCardWrapperDtoResponse;
import ru.stepagin.backend.dto.ProjectDetailsDtoResponse;
import ru.stepagin.backend.dto.UpdateProjectDtoRequest;
import ru.stepagin.backend.entity.ProjectVersionEntity;
import ru.stepagin.backend.mapper.ProjectMapper;
import ru.stepagin.backend.service.ProjectService;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("${app.path.start-prefix}/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<ProjectCardWrapperDtoResponse> getProjects(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit
    ) {
        return ResponseEntity.ok(projectService.getAllProjects(query, PageRequest.of(page, limit)));
    }

    @GetMapping("/{author}/{name}")
    public ResponseEntity<ProjectDetailsDtoResponse> getProjectDetails(
            @PathVariable(name = "author") String author,
            @PathVariable(name = "name") String projectName,
            @RequestParam(value = "version", required = false) String version
    ) {
        ProjectVersionEntity project = projectService.getProject(author, projectName, version);
        return ResponseEntity.ok(ProjectMapper.toDto(project));
    }

    @PostMapping
    public ResponseEntity<ProjectDetailsDtoResponse> createProject(
            @RequestBody CreateProjectDtoRequest request,
            Principal principal
    ) {
        String username = principal.getName();
        ProjectVersionEntity project = projectService.createProject(request, username);
        return ResponseEntity.ok(ProjectMapper.toDto(project));
    }

    @DeleteMapping("/{author}/{name}")
    public ResponseEntity<ProjectDetailsDtoResponse> deleteProject(
            @PathVariable(name = "author") String author,
            @PathVariable(name = "name") String projectName,
            Principal principal
    ) {
        String username = principal.getName();
        if (!author.equals(username)) {
            throw new IllegalArgumentException("Can delete only your own projects");
        }
        projectService.deleteProject(author, projectName);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{author}/{name}")
    public ResponseEntity<ProjectDetailsDtoResponse> updateProject(
            @RequestBody UpdateProjectDtoRequest request,
            @PathVariable(name = "author") String author,
            @PathVariable(name = "name") String projectName,
            @RequestParam(name = "version", required = false) String version,
            Principal principal
    ) {
        String username = principal.getName();
        if (!author.equals(username)) {
            throw new IllegalArgumentException("Can update only your own projects");
        }
        ProjectVersionEntity updated = projectService.updateProjectData(request, author, projectName, version);
        return ResponseEntity.ok(ProjectMapper.toDto(updated));
    }
}
