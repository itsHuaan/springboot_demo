package org.example.springboot_demo.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.example.springboot_demo.configurations.jwtConfig.JwtProvider;
import org.example.springboot_demo.dtos.OtpDto;
import org.example.springboot_demo.mappers.impl.OtpMapper;
import org.example.springboot_demo.models.EmailModel;
import org.example.springboot_demo.models.OtpModel;
import org.example.springboot_demo.models.RegistrationRequest;
import org.example.springboot_demo.services.impl.EmailService;
import org.example.springboot_demo.services.impl.OtpService;
import org.example.springboot_demo.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/login")
public class RegistrationController {
    private final OtpService otpService;
    private final EmailService emailService;
    private final OtpMapper otpMapper;
    private final JwtProvider jwtProvider;

    @Autowired
    public RegistrationController(OtpService otpService, EmailService emailService, OtpMapper otpMapper, JwtProvider jwtProvider) {
        this.otpService = otpService;
        this.emailService = emailService;
        this.otpMapper = otpMapper;
        this.jwtProvider = jwtProvider;
    }

    @Operation(summary = "Sending confirmation email")
    @PostMapping
    public ResponseEntity<String> sendConfirmation(@RequestBody EmailModel emailModel) {
        boolean result = emailService.send(emailModel);
        if (result) {
            return new ResponseEntity<>("Email sent", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to sent", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Generate an OTP")
    @GetMapping("create_otp")
    public ResponseEntity<?> otp() {
        OtpModel otp = new OtpModel();
        otp.setOtpCode(otpService.generateOtp());
        OtpDto otpCode = otpService.save(otpMapper.toEntity(otp));
        return otpCode != null
                ? new ResponseEntity<>(otpCode, HttpStatus.OK)
                : new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Generate a token")
    @PostMapping("create_token")
    public ResponseEntity<?> token(@RequestBody RegistrationRequest registrationRequest) {
        String name = registrationRequest.getName();
        String username = registrationRequest.getUsername();
        String email = registrationRequest.getEmail();

        String registrationToken = jwtProvider.generateRegistrationToken(
                name,
                username,
                email);
        return registrationToken != null && !registrationToken.isEmpty()
                ? new ResponseEntity<>(registrationToken, HttpStatus.OK)
                : new ResponseEntity<>("Failed to generate token", HttpStatus.BAD_REQUEST);
    }
}
