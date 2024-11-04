package org.example.springboot_demo.controllers;

import org.example.springboot_demo.entities.StudentEntity;
import org.example.springboot_demo.repositories.IStudentRepository;
import org.example.springboot_demo.services.impl.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/student/v1")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<?> getStudent(){
        return ResponseEntity.ok(studentService.findAll());
    }
}

