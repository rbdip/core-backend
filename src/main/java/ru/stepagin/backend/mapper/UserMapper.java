package ru.stepagin.backend.mapper;

import ru.stepagin.backend.dto.UserCardDtoResponse;
import ru.stepagin.backend.entity.UserEntity;

public class UserMapper {

    public static UserCardDtoResponse toCard(UserEntity entity) {
        UserCardDtoResponse dto = new UserCardDtoResponse();
        dto.setId(entity.getId());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setUsername(entity.getUsername());
        dto.setProjects(entity.getProjects().stream().map(ProjectMapper::toDto).toList());
        dto.setDisplayName(entity.getDisplayName());
        return dto;
    }

}
