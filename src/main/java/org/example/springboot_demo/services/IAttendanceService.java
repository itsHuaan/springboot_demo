package org.example.springboot_demo.services;

import org.example.springboot_demo.dtos.AttendanceByDate;
import org.example.springboot_demo.dtos.AttendanceStatisticsDto;
import org.example.springboot_demo.dtos.AttendanceDto;
import org.example.springboot_demo.entities.AttendanceEntity;
import org.example.springboot_demo.models.Email;

import java.time.LocalDate;
import java.util.List;

public interface IAttendanceService extends IBaseService<AttendanceDto, AttendanceEntity, Long> {
    List<AttendanceDto> getByDate(LocalDate date);
    boolean canGrantPaidLeave(Long employeeId, int month, int year);
    List<AttendanceStatisticsDto> getStatistics(Long employeeId, Integer month, Integer year);
    List<AttendanceByDate> getAttendanceGroupByDate();
    List<AttendanceDto> getByEmployeeId(Long employeeId);
    List<AttendanceDto> getByEmployeeIdAndDate(Long employeeId, LocalDate date);
    boolean sendEmail(Email email, List<AttendanceStatisticsDto> overviewStatistics);
}
