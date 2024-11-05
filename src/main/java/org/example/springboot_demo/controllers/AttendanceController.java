package org.example.springboot_demo.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.example.springboot_demo.mappers.impl.AttendanceMapper;
import org.example.springboot_demo.models.AttendanceModel;
import org.example.springboot_demo.models.Notes;
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
    private final LocalTime defaultCheckIn = LocalTime.of(8, 0, 0);

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
    public ResponseEntity<?> getAttendance(@RequestParam(required = false) LocalDate date) {
        return date != null
                ? ResponseEntity.ok(attendanceService.getByDate(date))
                : ResponseEntity.ok(attendanceService.getAttendanceGroupByDate());
    }

    @Operation(summary = "Checking In")
    @PostMapping("checkin")
    public ResponseEntity<?> checkIn(@RequestBody AttendanceModel attendanceModel) {
        attendanceModel.setCheckIn(LocalTime.now());
        String checkInStatus = "";
        LocalTime late = defaultCheckIn.plusMinutes(30);
        LocalTime absent = defaultCheckIn.plusHours(1);
        if (attendanceModel.getCheckIn().isAfter(absent)) {
            checkInStatus += Notes.absent.toString();
            attendanceModel.setPaidLeave(attendanceService.canGrantPaidLeave(
                    attendanceModel.getEmployeeId(),
                    attendanceModel.getDate().getMonthValue(),
                    attendanceModel.getDate().getYear()
            ));
        } else if (attendanceModel.getCheckIn().isAfter(late)) {
            checkInStatus += Notes.lateArrival.toString();
            attendanceModel.setPaidLeave(false);
        } else {
            checkInStatus += Notes.onTime.toString();
            attendanceModel.setPaidLeave(false);
        }
        attendanceModel.setCheckInStatus(checkInStatus);
        return attendanceService.save(mapper.toEntity(attendanceModel)) != null
                ? new ResponseEntity<>(attendanceModel, HttpStatus.CREATED)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Checking Out")
    @PutMapping("checkout")
    public ResponseEntity<?> checkOut(@RequestBody AttendanceModel attendanceModel) {
        return attendanceService.checkingOut(mapper.toEntity(attendanceModel)) != null
                ? new ResponseEntity<>(attendanceModel, HttpStatus.OK)
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
