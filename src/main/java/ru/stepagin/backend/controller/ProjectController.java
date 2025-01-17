package ru.stepagin.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.backend.dto.CreateProjectDtoRequest;
import ru.stepagin.backend.dto.ProjectCardDtoResponse;
import ru.stepagin.backend.dto.ProjectDetailsDtoResponse;
import ru.stepagin.backend.entity.ProjectCardEntity;
import ru.stepagin.backend.entity.ProjectVersionEntity;
import ru.stepagin.backend.mapper.ProjectMapper;
import ru.stepagin.backend.service.ProjectService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("${app.path.start-prefix}/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    public List<ProjectCardDtoResponse> getProjects() { // todo: фильтрация и сортировка
        List<ProjectCardEntity> projects = projectService.getAllProjects();
        return projects.stream().map(ProjectMapper::toDto).toList();
    }

    @GetMapping("/{author}/{name}")
    public ProjectDetailsDtoResponse getProjectDetails(
            @PathVariable(name = "author") String author,
            @PathVariable(name = "name") String projectName,
            @RequestParam(value = "version", required = false) String version
    ) { // todo: фильтрация и сортировка
        ProjectVersionEntity project = projectService.getProject(author, projectName, version);
        return ProjectMapper.toDto(project);
    }

    @PostMapping
    public ProjectDetailsDtoResponse createProject(
            @RequestBody CreateProjectDtoRequest request
    ) {
        ProjectVersionEntity project = projectService.createProject(request, "admin");
        return ProjectMapper.toDto(project);
    }


}