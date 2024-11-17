package org.example.springboot_demo.services;

import org.example.springboot_demo.dtos.EmployeeDto;
import org.example.springboot_demo.entities.EmployeeEntity;

public interface IEmployeeService extends IBaseService<EmployeeDto, EmployeeEntity, Long> {
    EmployeeDto findByUsername(String username);
    boolean isExisting(String username, String email);
}
