package ru.stepagin.backend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProjectCardWrapperDtoResponse {
    private List<ProjectCardDtoResponse> projects;
    private Long totalElements;
    private Long totalPages;

    public ProjectCardWrapperDtoResponse(List<ProjectCardDtoResponse> projects) {
        this.projects = projects;
        this.totalElements = (long) projects.size();
        this.totalPages = null;
    }
}
