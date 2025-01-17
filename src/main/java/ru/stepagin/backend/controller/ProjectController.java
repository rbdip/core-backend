package ru.stepagin.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.backend.dto.CreateProjectDtoRequest;
import ru.stepagin.backend.dto.ProjectCardDtoResponse;
import ru.stepagin.backend.dto.ProjectDetailsDtoResponse;
import ru.stepagin.backend.entity.ProjectCardEntity;
import ru.stepagin.backend.entity.ProjectVersionEntity;
import ru.stepagin.backend.mapper.ProjectMapper;
import ru.stepagin.backend.service.ProjectService;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("${app.path.start-prefix}/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectCardDtoResponse>> getProjects() { // todo: фильтрация и сортировка
        List<ProjectCardEntity> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects.stream().map(ProjectMapper::toDto).toList());
    }

    @GetMapping("/{author}/{name}")
    public ResponseEntity<ProjectDetailsDtoResponse> getProjectDetails(
            @PathVariable(name = "author") String author,
            @PathVariable(name = "name") String projectName,
            @RequestParam(value = "version", required = false) String version
    ) { // todo: фильтрация и сортировка
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
            throw new IllegalArgumentException("can delete only your own projects");
        }
        projectService.deleteProject(author, projectName);
        return ResponseEntity.noContent().build();
    }
}
