package org.example.springboot_demo.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.example.springboot_demo.dtos.EmployeeDto;
import org.example.springboot_demo.mappers.impl.EmployeeMapper;
import org.example.springboot_demo.models.EmployeeModel;
import org.example.springboot_demo.services.impl.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/employee/v1")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeMapper mapper;

    @Autowired
    public EmployeeController(EmployeeService employeeService, EmployeeMapper mapper) {
        this.employeeService = employeeService;
        this.mapper = mapper;
    }

    @Operation(summary = "Get All Employees")
    @GetMapping
    public ResponseEntity<?> getEmployee(){
        List<EmployeeDto> result = employeeService.findAll();
        return result != null && !result.isEmpty()
                ? new ResponseEntity<>(result, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Add An Employee")
    @PostMapping
    public ResponseEntity<?> addEmployee(@RequestBody EmployeeModel employeeModel){
        EmployeeDto result = employeeService.save(mapper.toEntity(employeeModel));
        return result != null
                ? new ResponseEntity<>(result, HttpStatus.CREATED)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}

