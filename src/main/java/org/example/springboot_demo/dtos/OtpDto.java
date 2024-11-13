package org.example.springboot_demo.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpDto {
    private long id;

    private String otpCode;

    private LocalDateTime createdAt;

    private int status;
}
