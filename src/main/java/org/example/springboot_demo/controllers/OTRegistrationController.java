package org.example.springboot_demo.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jdk.dynalink.linker.LinkerServices;
import org.example.springboot_demo.dtos.OTRegistrationDto;
import org.example.springboot_demo.mappers.impl.OTRegistrationMapper;
import org.example.springboot_demo.models.OTRegistrationModel;
import org.example.springboot_demo.services.impl.OTRegistrationService;
import org.example.springboot_demo.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/ot")
public class OTRegistrationController {
    private final OTRegistrationService otRegistrationService;
    private final OTRegistrationMapper otRegistrationMapper;

    @Autowired

    public OTRegistrationController(OTRegistrationService otRegistrationService, OTRegistrationMapper otRegistrationMapper) {
        this.otRegistrationService = otRegistrationService;
        this.otRegistrationMapper = otRegistrationMapper;
    }

    @Operation(summary = "Get OT Registrations")
    @GetMapping
    public ResponseEntity<?> getOTRegistration() {
        List<OTRegistrationDto> result = otRegistrationService.findAll();
        return result != null && !result.isEmpty()
                ? new ResponseEntity<>(result, HttpStatus.OK)
                : new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


    @Operation(summary = "Register for OT")
    @PostMapping
    public ResponseEntity<?> registerForOT(@RequestBody OTRegistrationModel registration) {
        OTRegistrationDto result = otRegistrationService.save(otRegistrationMapper.toEntity(registration));
        return result != null
                ? new ResponseEntity<>(result, HttpStatus.CREATED)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Update an OT Registration")
    @PutMapping("update")
    public ResponseEntity<?> updateOT(@RequestParam Long id, @RequestBody OTRegistrationModel registration) {
        OTRegistrationDto registrationDto = otRegistrationService.findById(id);
        if (registrationDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        registration.setEmployeeId(registrationDto.getEmployeeId());
        registration.setDate(registrationDto.getDate());
        registration.setStartTime(registrationDto.getStartTime());
        registration.setEndTime(registrationDto.getEndTime());
        registration.setReason(registrationDto.getReason());

        OTRegistrationDto result = otRegistrationService.save(otRegistrationMapper.toEntity(registration));
        return result != null
                ? new ResponseEntity<>(result, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
