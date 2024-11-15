package org.example.springboot_demo.mappers.impl;

import org.example.springboot_demo.dtos.EmployeeDto;
import org.example.springboot_demo.entities.EmployeeEntity;
import org.example.springboot_demo.entities.RoleEntity;
import org.example.springboot_demo.mappers.IBaseMapper;
import org.example.springboot_demo.models.EmployeeModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper implements IBaseMapper<EmployeeDto, EmployeeModel, EmployeeEntity> {
    private final PasswordEncoder passwordEncoder;

    public EmployeeMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public EmployeeDto toDTO(EmployeeEntity employeeEntity) {
        return EmployeeDto.builder()
                .employeeId(employeeEntity.getEmployeeId())
                .name(employeeEntity.getName())
                .username(employeeEntity.getUsername())
                .email(employeeEntity.getEmail())
                .roleId(employeeEntity.getRole().getId())
                .unusedPaidLeaves(employeeEntity.getUnusedPaidLeaves())
                .isActive(employeeEntity.isActive())
                .build();
    }

    @Override
    public EmployeeEntity toEntity(EmployeeModel employeeModel) {
        RoleEntity role = new RoleEntity();
        role.setId(employeeModel.getRoleId());
        return EmployeeEntity.builder()
                .name(employeeModel.getName())
                .username(employeeModel.getUsername())
                .email(employeeModel.getEmail())
                .password(passwordEncoder.encode(employeeModel.getPassword()))
                .unusedPaidLeaves(employeeModel.getUnusedPaidLeaves())
                .role(role)
                .active(employeeModel.isActive())
                .build();
    }
}
