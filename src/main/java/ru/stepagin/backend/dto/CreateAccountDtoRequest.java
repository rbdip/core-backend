package ru.stepagin.backend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
public class CreateAccountDtoRequest {
    @NotNull
    @Size(min = 5, max = 32)
    @Pattern(regexp = "[a-zA-Z\\-_0-9]")
    private String username;

    @NotNull
    @Size(min = 8, max = 255)
    private String password;

    private String displayName;

}
