package org.example.springboot_demo.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.example.springboot_demo.configurations.UserDetailsImpl;
import org.example.springboot_demo.configurations.jwtConfig.JwtProvider;
import org.example.springboot_demo.dtos.EmployeeDto;
import org.example.springboot_demo.mappers.impl.EmployeeMapper;
import org.example.springboot_demo.models.EmployeeModel;
import org.example.springboot_demo.models.SigninRequest;
import org.example.springboot_demo.models.SigninResponse;
import org.example.springboot_demo.services.impl.EmployeeService;
import org.example.springboot_demo.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/employee")
public class EmployeeController {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtUtils;

    private final EmployeeService employeeService;
    private final EmployeeMapper mapper;

    @Autowired
    public EmployeeController(AuthenticationManager authenticationManager, JwtProvider jwtUtils, EmployeeService employeeService, EmployeeMapper mapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.employeeService = employeeService;
        this.mapper = mapper;
    }

    @Operation(summary = "Get All Employees")
    @GetMapping
    public ResponseEntity<?> getEmployee() {
        List<EmployeeDto> result = employeeService.findAll();
        return result != null && !result.isEmpty()
                ? new ResponseEntity<>(result, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Add An Employee")
    @PostMapping
    public ResponseEntity<?> addEmployee(@RequestBody EmployeeModel employeeModel) {
        boolean isExisting = employeeService.isExisting(employeeModel.getUsername(), employeeModel.getEmail());
        EmployeeDto result = !isExisting ? employeeService.save(mapper.toEntity(employeeModel)) : null;
        return result != null
                ? new ResponseEntity<>(result, HttpStatus.CREATED)
                : new ResponseEntity<>("Email or username is used by another employee", HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Sign In")
    @PostMapping("sign_in")
    public ResponseEntity<?> sign_in(@RequestBody SigninRequest credentials) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateTokenByUsername(userDetails.getUsername());
        return ResponseEntity.ok(new SigninResponse(userDetails.getEmployee().getEmployeeId(),
                "Bearer",
                jwt,
                userDetails.getUsername(),
                userDetails.getEmployee().getEmail(),
                userDetails.getEmployee().isActive(),
                userDetails.getRoleName()));
    }
}

