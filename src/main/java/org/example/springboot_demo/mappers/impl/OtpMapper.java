package org.example.springboot_demo.mappers.impl;

import org.example.springboot_demo.dtos.OtpDto;
import org.example.springboot_demo.entities.OtpEntity;
import org.example.springboot_demo.mappers.IBaseMapper;
import org.example.springboot_demo.models.OtpModel;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class OtpMapper implements IBaseMapper<OtpDto, OtpModel, OtpEntity> {
    @Override
    public OtpDto toDTO(OtpEntity entity) {
        return OtpDto.builder()
                .otpCode(entity.getOtpCode())
                .build();
    }

    @Override
    public OtpEntity toEntity(OtpModel model) {
        return OtpEntity.builder()
                .otpCode(model.getOtpCode())
                .createdAt(Timestamp.valueOf(model.getCreatedAt()))
                .status(model.getStatus())
                .build();
    }
}
