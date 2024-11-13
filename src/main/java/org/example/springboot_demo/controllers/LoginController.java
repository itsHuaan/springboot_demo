package org.example.springboot_demo.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.example.springboot_demo.mappers.impl.OtpMapper;
import org.example.springboot_demo.models.EmailModel;
import org.example.springboot_demo.models.OtpModel;
import org.example.springboot_demo.services.impl.EmailService;
import org.example.springboot_demo.services.impl.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/login/v1")
public class LoginController {
    private final OtpService otpService;
    private final EmailService emailService;
    private final OtpMapper otpMapper;

    @Autowired
    public LoginController(OtpService otpService, EmailService emailService, OtpMapper otpMapper) {
        this.otpService = otpService;
        this.emailService = emailService;
        this.otpMapper = otpMapper;
    }

    @Operation(summary = "Sending an OTP to email")
    @PostMapping
    public ResponseEntity<String> sendOtp(@RequestBody EmailModel emailModel) {
        OtpModel otp = new OtpModel();
        otp.setOtpCode(otpService.generateOtp());
        emailModel.setContent(otp.getOtpCode());
        boolean result = emailService.send(emailModel);
        if (result) {
            otpService.save(otpMapper.toEntity(otp));
            return new ResponseEntity<>("Email sent", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to sent", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Generate an OTP")
    @GetMapping
    public ResponseEntity<String> otp() {
        return new ResponseEntity<>(otpService.generateOtp(), HttpStatus.OK);
    }
}