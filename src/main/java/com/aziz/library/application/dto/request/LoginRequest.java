package com.aziz.library.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Login request payload")
public class LoginRequest {

    @NotBlank(message = "Identifier (username or email) is required")
    @Schema(description = "Username or email", example = "Abdulaziz")
    private String identifier;
    
    @NotBlank(message = "Password is required")
    @Schema(description = "User password", example = "Password123!")
    private String password;

}
