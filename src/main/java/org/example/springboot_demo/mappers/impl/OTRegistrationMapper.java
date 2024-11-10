package org.example.springboot_demo.mappers.impl;

import org.example.springboot_demo.dtos.OTRegistrationDto;
import org.example.springboot_demo.entities.EmployeeEntity;
import org.example.springboot_demo.entities.OTRegistrationEntity;
import org.example.springboot_demo.mappers.IBaseMapper;
import org.example.springboot_demo.models.OTRegistrationModel;
import org.springframework.stereotype.Component;

@Component
public class OTRegistrationMapper implements IBaseMapper<OTRegistrationDto, OTRegistrationModel, OTRegistrationEntity> {
    @Override
    public OTRegistrationDto toDTO(OTRegistrationEntity entity) {
        return OTRegistrationDto.builder()
                .employee(entity.getEmployee().getName())
                .date(entity.getDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .build();
    }

    @Override
    public OTRegistrationEntity toEntity(OTRegistrationModel model) {
        EmployeeEntity employeeEntity = new EmployeeEntity();
        employeeEntity.setEmployeeId(model.getEmployeeId());
        return OTRegistrationEntity.builder()
                .employee(employeeEntity)
                .date(model.getDate())
                .startTime(model.getStartTime())
                .endTime(model.getEndTime())
                .reason(model.getReason())
                .status(model.getStatus())
                .build();
    }
}
