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

    @Autowired
    public EmployeeService(IEmployeeRepository iEmployeeRepository,
                           EmployeeMapper employeeMapper) {
        this.iEmployeeRepository = iEmployeeRepository;
        this.employeeMapper = employeeMapper;
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
}
