package org.example.springboot_demo.mappers.impl;

import org.example.springboot_demo.dtos.EmployeeDto;
import org.example.springboot_demo.entities.EmployeeEntity;
import org.example.springboot_demo.mappers.IBaseMapper;
import org.example.springboot_demo.models.EmployeeModel;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper implements IBaseMapper<EmployeeDto, EmployeeModel, EmployeeEntity> {
    @Override
    public EmployeeDto toDTO(EmployeeEntity employeeEntity) {
        return EmployeeDto.builder()
                .employeeId(employeeEntity.getEmployeeId())
                .name(employeeEntity.getName())
                .email(employeeEntity.getEmail())
                .unusedPaidLeaves(employeeEntity.getUnusedPaidLeaves())
                .build();
    }

    @Override
    public EmployeeEntity toEntity(EmployeeModel employeeModel) {
        return EmployeeEntity.builder()
                .name(employeeModel.getName())
                .email(employeeModel.getEmail())
                .unusedPaidLeaves(employeeModel.getUnusedPaidLeaves())
                .build();
    }
}
