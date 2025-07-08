package org.example.backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@ToString(exclude = "password")
@EqualsAndHashCode(exclude = "password")
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "Username is mandatory")
    private String username;

    @Email(message = "Email is not valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    private String password;
}
