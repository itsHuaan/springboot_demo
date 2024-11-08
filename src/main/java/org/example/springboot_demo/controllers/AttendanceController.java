package org.example.springboot_demo.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.example.springboot_demo.dtos.AttendanceDto;
import org.example.springboot_demo.mappers.impl.AttendanceMapper;
import org.example.springboot_demo.models.AttendanceModel;
import org.example.springboot_demo.services.impl.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("api/attendance/v1")
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final AttendanceMapper mapper;

    @Autowired
    public AttendanceController(final AttendanceService attendanceService,
                                final AttendanceMapper mapper) {
        this.attendanceService = attendanceService;
        this.mapper = mapper;
    }

    @Operation(summary = "Get All Attendances")
    @GetMapping
    public ResponseEntity<?> getAttendance(@RequestParam(required = false) Integer day,
                                           @RequestParam(required = false) Integer month,
                                           @RequestParam(required = false) Integer year) {
        LocalDate date = day != null && month != null && year != null
                ? LocalDate.of(year, month, day)
                : null;
        return date != null
                ? ResponseEntity.ok(attendanceService.getByDate(date))
                : ResponseEntity.ok(attendanceService.getAttendanceGroupByDate());
    }

    @Operation(summary = "Save Attendance (Check-in or Check-out)")
    @PostMapping("save")
    public ResponseEntity<?> checkIn(@RequestBody AttendanceModel attendanceModel) {
        attendanceModel.setDate(attendanceModel.getDate() != null
                ? attendanceModel.getDate()
                : LocalDate.now());
        attendanceModel.setCheckIn(attendanceModel.getCheckIn() != null
                ? attendanceModel.getCheckIn()
                : LocalTime.now());
        AttendanceDto result = attendanceService.save(mapper.toEntity(attendanceModel));
        return result != null
                ? new ResponseEntity<>(result, HttpStatus.CREATED)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Get Attendance Statistics")
    @GetMapping("statistics")
    public ResponseEntity<?> getAttendanceStatistics(@RequestParam(required = false) Long employeeId,
                                                     @RequestParam(required = false) Integer month,
                                                     @RequestParam(required = false) Integer year) {
        return new ResponseEntity<>(attendanceService.getStatistics(employeeId, month, year), HttpStatus.OK);
    }
}
