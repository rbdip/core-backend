package ru.stepagin.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.stepagin.backend.dto.ProjectCardDtoResponse;
import ru.stepagin.backend.dto.ProjectDetailsDtoResponse;
import ru.stepagin.backend.entity.ProjectCardEntity;
import ru.stepagin.backend.entity.ProjectVersionEntity;

import java.util.List;
import java.util.Set;

@Mapper
public interface ProjectMapper {
    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(target = "author", expression = "java(entity.getAuthor().getUsername())")
    @Mapping(target = "authorDisplayName", expression = "java(entity.getAuthor().getDisplayName())")
    ProjectCardDtoResponse toDto(ProjectCardEntity entity);

    @Mapping(target = "id", expression = "java(entity.getProjectCard().getId())")
    @Mapping(target = "title", expression = "java(entity.getProjectCard().getTitle())")
    @Mapping(target = "name", expression = "java(entity.getProjectCard().getName())")
    @Mapping(target = "authorUsername", expression = "java(entity.getProjectCard().getAuthor().getUsername())")
    @Mapping(target = "authorDisplayName", expression = "java(entity.getProjectCard().getAuthor().getDisplayName())")
    @Mapping(target = "createdOn", expression = "java(entity.getProjectCard().getCreatedOn())")
    @Mapping(target = "updatedOn", expression = "java(entity.getProjectCard().getUpdatedOn())")
    @Mapping(target = "displayVersion", source = "entity.projectCard.projectVersions", qualifiedByName = "firstDisplayName")
    @Mapping(target = "versions", source = "entity.projectCard.projectVersions", qualifiedByName = "toProjectVersionDtos")
    ProjectDetailsDtoResponse toDto(ProjectVersionEntity entity);

    @Named("firstDisplayName")
    default String firstDisplayName(Set<ProjectVersionEntity> versions) {
        return versions.stream()
                .limit(1)
                .findFirst()
                .map(ProjectVersionEntity::getDisplayName)
                .orElse(null);
    }

    @Named("toProjectVersionDtos")
    default List<ProjectDetailsDtoResponse.projectVersionDto> toProjectVersionDtos(Set<ProjectVersionEntity> versions) {
        return versions.stream()
                .map(v -> new ProjectDetailsDtoResponse.projectVersionDto(v.getId(), v.getDisplayName(), v.getDisplayOrder()))
                .toList();
    }

}
