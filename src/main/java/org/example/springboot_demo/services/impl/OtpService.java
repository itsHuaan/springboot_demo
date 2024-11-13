package org.example.springboot_demo.services.impl;

import org.example.springboot_demo.dtos.OtpDto;
import org.example.springboot_demo.entities.OtpEntity;
import org.example.springboot_demo.mappers.impl.OtpMapper;
import org.example.springboot_demo.repositories.IOtpRepository;
import org.example.springboot_demo.services.IOtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;

import static org.example.springboot_demo.utils.specifications.OtpSpecifications.equalTo;
import static org.example.springboot_demo.utils.specifications.OtpSpecifications.isActive;

@Service
public class OtpService implements IOtpService {
    private final IOtpRepository otpRepository;
    private final OtpMapper otpMapper;

    @Autowired
    public OtpService(IOtpRepository otpRepository,
                      OtpMapper otpMapper) {
        this.otpRepository = otpRepository;
        this.otpMapper = otpMapper;
    }

    @Override
    public List<OtpDto> findAll() {
        return List.of();
    }

    @Override
    public OtpDto findById(Long id) {
        return otpMapper.toDTO(Objects.requireNonNull(otpRepository.findById(id).orElse(null)));
    }

    @Override
    public OtpDto save(OtpEntity entity) {
        return otpMapper.toDTO(otpRepository.save(entity));
    }

    @Override
    public int delete(Long id) {
        OtpEntity otp = otpRepository.findById(id).orElse(null);
        assert otp != null;
        otp.setStatus(0);
        otpRepository.save(otp);
        return 0;
    }


    @Override
    public OtpDto findByConfirmationCode(String confirmationCode) {
        return otpMapper.toDTO(Objects.requireNonNull(otpRepository.findOne(Specification.where(isActive()).and(equalTo(confirmationCode))).orElse(null)));
    }

    @Override
    public String generateOtp(){
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        int OTP_LENGTH = 8;
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = secureRandom.nextInt(SALTCHARS.length());
            otp.append(SALTCHARS.charAt(index));
        }
        return otp.toString();
    }
}
