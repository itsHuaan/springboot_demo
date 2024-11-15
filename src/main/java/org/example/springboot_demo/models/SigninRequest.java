package org.example.springboot_demo.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SigninRequest {
    @Schema(title = "username", example = "admin")
    @NotBlank
    private String username;
    @Schema(title = "password", example = "admin")
    @NotBlank
    private String password;
}
