package org.example.springboot_demo.mappers.impl;

import org.example.springboot_demo.dtos.StudentDto;
import org.example.springboot_demo.entities.StudentEntity;
import org.example.springboot_demo.mappers.IBaseMapper;
import org.example.springboot_demo.models.StudentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper implements IBaseMapper<StudentDto, StudentModel, StudentEntity> {
    @Override
    public StudentDto toDTO(StudentEntity studentEntity) {

        return StudentDto.builder()
                .studentId(studentEntity.getStudentId())
                .name(studentEntity.getName())
                .build();
    }

    @Override
    public StudentEntity toEntity(StudentModel studentModel) {
        return StudentEntity.builder()
                .studentId(studentModel.getStudentId())
                .name(studentModel.getName())
                .build();
    }
}
