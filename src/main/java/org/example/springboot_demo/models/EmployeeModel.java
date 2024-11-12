package org.example.springboot_demo.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeModel {
    private String name;
    private String email;
    private int unusedPaidLeaves = 1;
}
