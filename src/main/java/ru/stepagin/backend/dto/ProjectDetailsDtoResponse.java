package ru.stepagin.backend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;
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
    private String title;
    private String name;
    private AuthorDto author;
    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;
    private String description;
    private String displayVersion;
    private List<ProjectVersionDto> versions;

    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ProjectVersionDto {
        private Long id;
        private String versionName;
        private Integer displayOrder;
    }
}