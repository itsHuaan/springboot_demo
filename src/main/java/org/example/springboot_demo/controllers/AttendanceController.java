package org.example.springboot_demo.controllers;

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

    @GetMapping
    public ResponseEntity<?> getAttendance(@RequestParam(required = false) LocalDate date) {
        return date != null
                ? ResponseEntity.ok(attendanceService.getByDate(date))
                : ResponseEntity.ok(attendanceService.findAll());
    }

    @PostMapping("checkin")
    public ResponseEntity<?> createAttendance(@RequestBody AttendanceModel attendanceModel) {
        attendanceModel.setCheckIn(LocalTime.now());
        String note = "";
        LocalTime late = defaultCheckIn.plusMinutes(30);
        LocalTime absent = defaultCheckIn.plusHours(1);
        if (attendanceModel.getCheckIn().isAfter(absent)) {
            note += ("" + Notes.absent);
        } else if (attendanceModel.getCheckIn().isAfter(late)) {
            note += ("" + Notes.lateArrival);
        } else {
            note += ("" + Notes.onTime);
        }
        attendanceModel.setNotes(note);
        return attendanceService.save(mapper.toEntity(attendanceModel)) != null
                ? new ResponseEntity<>(attendanceModel, HttpStatus.CREATED)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("checkout")
    public ResponseEntity<?> updateAttendance(@RequestBody AttendanceModel attendanceModel) {
        return attendanceService.checkingOut(mapper.toEntity(attendanceModel)) != null
                ? new ResponseEntity<>(attendanceModel, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
