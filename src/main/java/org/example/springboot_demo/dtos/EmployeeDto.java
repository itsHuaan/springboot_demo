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
    private int unusedPaidLeaves;
}
