package org.example.springboot_demo.services;

import org.example.springboot_demo.dtos.EmployeeDto;
import org.example.springboot_demo.entities.EmployeeEntity;

public interface IStudentService extends IBaseService<EmployeeDto, EmployeeEntity, Long> {
}
