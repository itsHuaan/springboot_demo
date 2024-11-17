package org.example.springboot_demo.services.impl;

import org.example.springboot_demo.configurations.UserDetailsImpl;
import org.example.springboot_demo.dtos.EmployeeDto;
import org.example.springboot_demo.entities.EmployeeEntity;
import org.example.springboot_demo.mappers.impl.EmployeeMapper;
import org.example.springboot_demo.repositories.IEmployeeRepository;
import org.example.springboot_demo.services.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class EmployeeService implements IEmployeeService, UserDetailsService {

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

    @Override
    public EmployeeDto findByUsername(String username) {
        return employeeMapper.toDTO(iEmployeeRepository.findByUsername(username));
    }

    @Override
    public boolean isExisting(String username, String email) {
        return iEmployeeRepository.findByEmailOrUsername(email, username) != null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserDetailsImpl(iEmployeeRepository.findByUsername(username));
    }
}
