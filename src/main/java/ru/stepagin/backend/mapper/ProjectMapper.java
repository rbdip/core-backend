package ru.stepagin.backend.mapper;

import ru.stepagin.backend.dto.AuthorDto;
import ru.stepagin.backend.dto.ProjectCardDtoResponse;
import ru.stepagin.backend.dto.ProjectDetailsDtoResponse;
import ru.stepagin.backend.entity.ProjectCardEntity;

public class ProjectMapper {

    public static ProjectCardDtoResponse toCardDto(ProjectCardEntity entity) {
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

        return dto;
    }

    public static ProjectDetailsDtoResponse toDetailsDto(ProjectCardEntity card) {
        if (card == null) {
            return new ProjectDetailsDtoResponse();
        }

        ProjectDetailsDtoResponse dto = new ProjectDetailsDtoResponse();
        dto.setTitle(card.getTitle());
        dto.setName(card.getName());
        if (card.getAuthor() != null) {
            AuthorDto authorDto = new AuthorDto();
            authorDto.setUsername(card.getAuthor().getUsername());
            authorDto.setDisplayName(card.getAuthor().getDisplayName());
            dto.setAuthor(authorDto);
        }
        dto.setCreatedOn(card.getCreatedOn() != null ? card.getCreatedOn() : null);
        dto.setUpdatedOn(card.getUpdatedOn() != null ? card.getUpdatedOn() : null);
        dto.setDescription(card.getDescription());

        return dto;
    }

}
