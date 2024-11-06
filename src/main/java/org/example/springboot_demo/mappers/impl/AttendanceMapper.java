package org.example.springboot_demo.mappers.impl;

import org.example.springboot_demo.dtos.AttendanceDto;
import org.example.springboot_demo.entities.AttendanceEntity;
import org.example.springboot_demo.entities.EmployeeEntity;
import org.example.springboot_demo.mappers.IBaseMapper;
import org.example.springboot_demo.models.AttendanceModel;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper implements IBaseMapper<AttendanceDto, AttendanceModel, AttendanceEntity> {
    @Override
    public AttendanceDto toDTO(AttendanceEntity attendanceEntity) {
        return AttendanceDto.builder()
                .employee(attendanceEntity.getEmployee().getName())
                .checkIn(attendanceEntity.getCheckIn())
                .checkInStatus(attendanceEntity.getCheckInStatus())
                .checkOut(attendanceEntity.getCheckOut())
                .checkOutStatus(attendanceEntity.getCheckOutStatus())
                .date(attendanceEntity.getDate())
                .notes(attendanceEntity.getNotes())
//                .isPaidLeave(attendanceEntity.isPaidLeave())
                .build();
    }

    @Override
    public AttendanceEntity toEntity(AttendanceModel attendanceModel) {
        EmployeeEntity employeeEntity = new EmployeeEntity();
        employeeEntity.setEmployeeId(attendanceModel.getEmployeeId());
        return AttendanceEntity.builder()
                .checkIn(attendanceModel.getCheckIn())
                .checkInStatus(attendanceModel.getCheckInStatus())
                .checkOut(attendanceModel.getCheckOut())
                .checkOutStatus(attendanceModel.getCheckOutStatus())
                .date(attendanceModel.getDate())
                .notes(attendanceModel.getNotes())
                .employee(employeeEntity)
                .isPaidLeave(attendanceModel.isPaidLeave())
                .build();
    }
}
