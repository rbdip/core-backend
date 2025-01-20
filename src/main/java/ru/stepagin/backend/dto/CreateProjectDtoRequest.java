package ru.stepagin.backend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateProjectDtoRequest {
    @NotNull
    @Size(min = 1, max = 255)
    private String title;

    @NotNull
    @NotEmpty
    @Size(min = 5, max = 255)
    @Pattern(regexp = "[a-zA-Z\\-_0-9]+")
    private String name;

    @NotNull
    private String description;
}
