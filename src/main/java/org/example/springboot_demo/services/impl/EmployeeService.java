package org.example.springboot_demo.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.springboot_demo.dtos.EmployeeDto;
import org.example.springboot_demo.entities.EmployeeEntity;
import org.example.springboot_demo.mappers.impl.EmployeeMapper;
import org.example.springboot_demo.models.Email;
import org.example.springboot_demo.repositories.IEmployeeRepository;
import org.example.springboot_demo.services.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class EmployeeService implements IEmployeeService {

    private final IEmployeeRepository iEmployeeRepository;
    private final EmployeeMapper employeeMapper;
    private final JavaMailSender mailSender;

    @Autowired
    public EmployeeService(IEmployeeRepository iEmployeeRepository,
                           EmployeeMapper employeeMapper,
                           JavaMailSender mailSender) {
        this.iEmployeeRepository = iEmployeeRepository;
        this.employeeMapper = employeeMapper;
        this.mailSender = mailSender;
    }

    @Override
    public List<EmployeeDto> findAll() {
        return iEmployeeRepository.findAll().stream().map(employeeMapper::toDTO).toList();
    }

    @Override
    public EmployeeDto findById(Long id) {
        return employeeMapper.toDTO(Objects.requireNonNull(iEmployeeRepository.findById(id).orElse(null)));
    }

    @Override
    public EmployeeDto save(EmployeeEntity employeeEntity) {
        return employeeMapper.toDTO(iEmployeeRepository.save(employeeEntity));
    }

    @Override
    public int delete(Long aLong) {
        return 0;
    }

    public boolean sendEmail(Email email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email.getRecipient());
            helper.setSubject(email.getSubject());
            helper.setText(email.getContent(), true);

            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }
}
