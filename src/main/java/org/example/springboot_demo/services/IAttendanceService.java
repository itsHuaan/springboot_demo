package org.example.springboot_demo.services;

import org.example.springboot_demo.dtos.AttendanceDto;
import org.example.springboot_demo.entities.AttendanceEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IAttendanceService extends IBaseService<AttendanceDto, AttendanceEntity, Long> {
    List<AttendanceDto> getByDate(LocalDate date);
    AttendanceDto checkingOut(AttendanceEntity attendance);
}
