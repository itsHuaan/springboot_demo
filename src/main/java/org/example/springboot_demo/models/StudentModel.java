package org.example.springboot_demo.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentModel {
    private long studentId;
    private String name;
    private int unusedPaidLeaves = 0;
}
