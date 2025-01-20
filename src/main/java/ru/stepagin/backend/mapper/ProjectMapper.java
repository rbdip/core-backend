package ru.stepagin.backend.mapper;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import ru.stepagin.backend.dto.AuthorDto;
import ru.stepagin.backend.dto.ProjectCardDtoResponse;
import ru.stepagin.backend.dto.ProjectDetailsDtoResponse;
import ru.stepagin.backend.dto.ProjectVersionDto;
import ru.stepagin.backend.entity.FavouriteProjectEntity;
import ru.stepagin.backend.entity.ProjectCardEntity;
import ru.stepagin.backend.entity.ProjectVersionEntity;
import ru.stepagin.backend.entity.UserEntity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProjectMapper {

    public static ProjectCardDtoResponse toDto(ProjectCardEntity entity) {
        if (entity == null) {
            return null;
        }

        ProjectCardDtoResponse dto = new ProjectCardDtoResponse();
        dto.setTitle(entity.getTitle());
        dto.setName(entity.getName());
        if (entity.getAuthor() != null) {
            AuthorDto authorDto = new AuthorDto();
            authorDto.setUsername(entity.getAuthor().getUsername());
            authorDto.setDisplayName(entity.getAuthor().getDisplayName());
            dto.setAuthor(authorDto);
        }
        dto.setCreatedOn(entity.getCreatedOn() != null ? entity.getCreatedOn() : null);

        dto.setLikeCount(getLikeCount(entity));
        dto.setLiked(getIsLiked(entity));

        return dto;
    }

    public static ProjectDetailsDtoResponse toDto(ProjectVersionEntity entity) {
        if (entity == null) {
            return null;
        }

        ProjectDetailsDtoResponse dto = new ProjectDetailsDtoResponse();
        ProjectCardEntity card = entity.getProjectCard();
        if (card != null) {
            ProjectVersionEntity displayVersion = entity;
            dto.setTitle(card.getTitle());
            dto.setName(card.getName());
            if (card.getAuthor() != null) {
                AuthorDto authorDto = new AuthorDto();
                authorDto.setUsername(card.getAuthor().getUsername());
                authorDto.setDisplayName(card.getAuthor().getDisplayName());
                dto.setAuthor(authorDto);
            }
            dto.setCreatedOn(card.getCreatedOn() != null ? card.getCreatedOn() : null);
            dto.setUpdatedOn(displayVersion.getUpdatedOn());
            dto.setDisplayVersion(displayVersion.getVersionName());
            dto.setVersions(toProjectVersionDtos(card.getProjectVersions()));
        }
        dto.setDescription(entity.getDescription());

        dto.setLikeCount(getLikeCount(entity.getProjectCard()));
        dto.setLiked(getIsLiked(entity.getProjectCard()));

        return dto;
    }

    private static ProjectVersionEntity firstDisplayName(Set<ProjectVersionEntity> versions) {
        return versions.stream()
                .findFirst()
                .orElse(null);
    }

    public static List<ProjectVersionDto> toProjectVersionDtos(Set<ProjectVersionEntity> versions) {
        return versions.stream()
                .map(v -> new ProjectVersionDto(
                                v.getVersionName(),
                                v.getDisplayOrder(),
                                v.getCreatedOn(),
                                v.getUpdatedOn()
                        )
                )
                .collect(Collectors.toList());
    }

    private static int getLikeCount(ProjectCardEntity card) {
        return card.getProjectLikes().size();
    }

    private static boolean getIsLiked(ProjectCardEntity card) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String actorName;
        if (principal instanceof UserDetails) {
            actorName = ((UserDetails) principal).getUsername();
        } else {
            return false;
        }

        return card.getProjectLikes().stream()
                .map(FavouriteProjectEntity::getUser)
                .map(UserEntity::getUsername)
                .anyMatch(username -> username.equals(actorName));

    }
}
