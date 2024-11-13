package org.example.springboot_demo.services;

import org.example.springboot_demo.dtos.OtpDto;
import org.example.springboot_demo.entities.OtpEntity;

public interface IOtpService extends IBaseService<OtpDto, OtpEntity, Long>{
    OtpDto findByConfirmationCode(String confirmationCode);
    String generateOtp();
}
