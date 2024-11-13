package org.example.springboot_demo.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.springboot_demo.models.EmailModel;
import org.example.springboot_demo.services.IEmailService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService{
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public boolean send(EmailModel emailModel) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(emailModel.getRecipient());
            helper.setSubject(emailModel.getSubject());
            helper.setText(emailModel.getContent(), true);
            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

    @Override
    public boolean sendWithAttachment(EmailModel emailModel, ByteArrayResource attachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(emailModel.getRecipient());
            helper.setSubject(emailModel.getSubject());
            helper.setText(emailModel.getContent(), true);
            helper.addAttachment("Bang cham cong.xlsx", attachment);
            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }
}
