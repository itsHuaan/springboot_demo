package org.example.springboot_demo.services.impl;

import org.example.springboot_demo.dtos.StudentDto;
import org.example.springboot_demo.entities.StudentEntity;
import org.example.springboot_demo.mappers.impl.StudentMapper;
import org.example.springboot_demo.models.StudentModel;
import org.example.springboot_demo.repositories.IStudentRepository;
import org.example.springboot_demo.services.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService implements IStudentService {

    private final IStudentRepository studentRepository;
    private final StudentMapper mapper;

    @Autowired
    public StudentService(IStudentRepository studentRepository,
                          StudentMapper mapper) {
        this.studentRepository = studentRepository;
        this.mapper = mapper;
    }

    @Override
    public List<StudentDto> findAll() {
        return studentRepository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    public StudentDto findById(Long aLong) {
        return null;
    }

    @Override
    public StudentDto save(StudentEntity studentEntity) {
        return null;
    }

    @Override
    public int delete(Long aLong) {
        return 0;
    }
}
