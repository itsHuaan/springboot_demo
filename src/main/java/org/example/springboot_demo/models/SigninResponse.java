package org.example.springboot_demo.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
@Setter
public class SigninResponse {
    private Long id;
    private String type = "Bearer";
    private String token;
    private String username;
    private String email;
    private Boolean isActive;
    private String roleName;
}
