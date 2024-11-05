package org.example.springboot_demo.services;

import org.example.springboot_demo.dtos.AttendanceByDate;
import org.example.springboot_demo.dtos.AttendanceStatisticsDto;
import org.example.springboot_demo.dtos.AttendanceDto;
import org.example.springboot_demo.entities.AttendanceEntity;

import java.time.LocalDate;
import java.util.List;

public interface IAttendanceService extends IBaseService<AttendanceDto, AttendanceEntity, Long> {
    List<AttendanceDto> getByDate(LocalDate date);
    AttendanceDto checkingOut(AttendanceEntity attendance);
    boolean canGrantPaidLeave(Long employeeId, int month, int year);
    List<AttendanceStatisticsDto> getStatistics(Long employeeId, int month, int year);
    List<AttendanceByDate> getAttendanceGroupByDate();
}
