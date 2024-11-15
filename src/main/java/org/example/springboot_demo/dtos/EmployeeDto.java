package org.example.springboot_demo.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {
    private long employeeId;
    private String name;
    private String username;
    private String email;
    private long roleId;
    private int unusedPaidLeaves;
    private boolean isActive;
}
