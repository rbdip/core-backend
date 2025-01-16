package ru.stepagin.backend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProjectDetailsDtoResponse {
    private Long id;
    private String projectTitle;
    private String projectName;
    private String authorUsername;
    private String authorDisplayName;
    private LocalDate createdOn;

    private LocalDate updatedOIn;
    private String description;
    private String displayVersion;
    private List<projectVersionDto> versions;

    static class projectVersionDto {
        private Long id;
        private String displayName;
        private Integer displayOrder;
    }
}