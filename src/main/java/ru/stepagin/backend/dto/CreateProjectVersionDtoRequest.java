package ru.stepagin.backend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateProjectVersionDtoRequest {
    @NotNull
    @Pattern(regexp = "[a-zA-Z\\-_0-9.]+")
    private String versionName;

    @NotNull
    private String description;

    private Integer displayOrder;
}
