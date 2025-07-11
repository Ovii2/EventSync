package org.example.backend.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 4, max = 100, message = "Title must be at least {min} and not exceed {max} characters")
    private String title;

    @Size(min = 4, max = 500, message = "Description must be at least {min} and not exceed {max} characters")
    private String description;
}
