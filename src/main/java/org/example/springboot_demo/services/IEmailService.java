package org.example.springboot_demo.services;

import org.example.springboot_demo.dtos.AttendanceStatisticsDto;
import org.example.springboot_demo.models.EmailModel;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

public interface IEmailService {
    boolean send(EmailModel emailModel);
    boolean sendWithAttachment(EmailModel emailModel, ByteArrayResource attachment);
}
