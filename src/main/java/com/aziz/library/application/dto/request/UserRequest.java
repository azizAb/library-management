package com.aziz.library.application.dto.request;

import com.aziz.library.domain.model.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User management request payload")
public class UserRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100)
    @Schema(description = "User's full name", example = "Abdul Aziz")
    private String fullname;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    @Schema(description = "Unique username", example = "Abdulaziz")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "User's email", example = "abdul.aziz@example.com")
    private String email;
    
    @Schema(description = "User password (optional for updates)", example = "NewPassword123!")
    private String password;
    
    @Schema(description = "User role", example = "EDITOR")
    private Role role;

}
