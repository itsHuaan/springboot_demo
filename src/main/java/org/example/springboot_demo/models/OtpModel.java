package org.example.springboot_demo.models;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpModel {
    private long id;

    private String otpCode;

    private LocalDateTime createdAt = LocalDateTime.now();

    private int status = 1;
}
