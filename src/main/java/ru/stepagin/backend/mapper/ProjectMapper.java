package ru.stepagin.backend.mapper;

import ru.stepagin.backend.dto.ProjectCardDtoResponse;
import ru.stepagin.backend.dto.ProjectDetailsDtoResponse;
import ru.stepagin.backend.entity.ProjectCardEntity;
import ru.stepagin.backend.entity.ProjectVersionEntity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProjectMapper {

    public static ProjectCardDtoResponse toDto(ProjectCardEntity entity) {
        if (entity == null) {
            return null;
        }

        ProjectCardDtoResponse dto = new ProjectCardDtoResponse();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setName(entity.getName());
        if (entity.getAuthor() != null) {
            dto.setAuthor(entity.getAuthor().getUsername());
            dto.setAuthorDisplayName(entity.getAuthor().getDisplayName());
        }
        dto.setCreatedOn(entity.getCreatedOn() != null ? entity.getCreatedOn().toLocalDate() : null);
        return dto;
    }

    public static ProjectDetailsDtoResponse toDto(ProjectVersionEntity entity) {
        if (entity == null) {
            return null;
        }

        ProjectDetailsDtoResponse dto = new ProjectDetailsDtoResponse();
        ProjectCardEntity card = entity.getProjectCard();
        if (card != null) {
            dto.setId(card.getId());
            dto.setTitle(card.getTitle());
            dto.setName(card.getName());
            if (card.getAuthor() != null) {
                dto.setAuthorUsername(card.getAuthor().getUsername());
                dto.setAuthorDisplayName(card.getAuthor().getDisplayName());
            }
            dto.setCreatedOn(card.getCreatedOn() != null ? card.getCreatedOn().toLocalDate() : null);
            dto.setUpdatedOn(card.getUpdatedOn() != null ? card.getUpdatedOn().toLocalDate() : null);
            dto.setDisplayVersion(firstDisplayName(card.getProjectVersions()));
            dto.setVersions(toProjectVersionDtos(card.getProjectVersions()));
        }
        dto.setDescription(entity.getDescription());
        return dto;
    }

    private static String firstDisplayName(Set<ProjectVersionEntity> versions) {
        return versions.stream()
                .findFirst()
                .map(ProjectVersionEntity::getDisplayName)
                .orElse(null);
    }

    private static List<ProjectDetailsDtoResponse.projectVersionDto> toProjectVersionDtos(Set<ProjectVersionEntity> versions) {
        return versions.stream()
                .map(version -> new ProjectDetailsDtoResponse.projectVersionDto(
                        version.getId(),
                        version.getDisplayName(),
                        version.getDisplayOrder()))
                .collect(Collectors.toList());
    }
}
