package org.example.springboot_demo.services.impl;

import org.example.springboot_demo.dtos.EmployeeDto;
import org.example.springboot_demo.entities.EmployeeEntity;
import org.example.springboot_demo.mappers.impl.EmployeeMapper;
import org.example.springboot_demo.repositories.IEmployeeRepository;
import org.example.springboot_demo.services.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class EmployeeService implements IEmployeeService {

    private final IEmployeeRepository iEmployeeRepository;
    private final EmployeeMapper mapper;

    @Autowired
    public EmployeeService(IEmployeeRepository iEmployeeRepository,
                           EmployeeMapper mapper) {
        this.iEmployeeRepository = iEmployeeRepository;
        this.mapper = mapper;
    }

    @Override
    public List<EmployeeDto> findAll() {
        return iEmployeeRepository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    public EmployeeDto findById(Long id) {
        return mapper.toDTO(Objects.requireNonNull(iEmployeeRepository.findById(id).orElse(null)));
    }

    @Override
    public EmployeeDto save(EmployeeEntity employeeEntity) {
        return mapper.toDTO(iEmployeeRepository.save(employeeEntity));
    }

    @Override
    public int delete(Long aLong) {
        return 0;
    }
}
