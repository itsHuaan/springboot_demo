package org.example.springboot_demo.services;

import org.example.springboot_demo.dtos.StudentDto;
import org.example.springboot_demo.entities.StudentEntity;
import org.example.springboot_demo.models.StudentModel;

public interface IStudentService extends IBaseService<StudentDto, StudentEntity, Long> {
}
