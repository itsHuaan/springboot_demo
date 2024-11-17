package org.example.springboot_demo.models;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeModel {
    private String name;
    private String username;
    private String email;
    private String password;
    private int unusedPaidLeaves = 1;
    private long roleId = 2;
    private boolean isActive = true;
}
