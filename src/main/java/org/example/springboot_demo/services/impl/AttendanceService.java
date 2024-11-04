package org.example.springboot_demo.services.impl;

import org.apache.catalina.mapper.Mapper;
import org.example.springboot_demo.dtos.AttendanceDto;
import org.example.springboot_demo.entities.AttendanceEntity;
import org.example.springboot_demo.mappers.impl.AttendanceMapper;
import org.example.springboot_demo.models.Notes;
import org.example.springboot_demo.repositories.IAttendanceRepository;
import org.example.springboot_demo.services.IAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AttendanceService implements IAttendanceService {

    private final IAttendanceRepository iAttendanceRepository;
    private final AttendanceMapper mapper;

    @Autowired
    public AttendanceService(final IAttendanceRepository iAttendanceRepository,
                             final AttendanceMapper mapper) {
        this.iAttendanceRepository = iAttendanceRepository;
        this.mapper = mapper;
    }

    @Override
    public List<AttendanceDto> findAll() {
        return iAttendanceRepository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    public AttendanceDto findById(Long id) {
        return mapper.toDTO(Objects.requireNonNull(iAttendanceRepository.findById(id).orElse(null)));
    }

    @Override
    public AttendanceDto save(AttendanceEntity attendanceEntity) {
        AttendanceEntity attendance = iAttendanceRepository.save(attendanceEntity);
        return mapper.toDTO(attendance);
    }

    @Override
    public int delete(Long aLong) {
        return 0;
    }

    @Override
    public List<AttendanceDto> getByDate(LocalDate date) {
        return iAttendanceRepository.findByDate(date).stream().map(mapper::toDTO).toList();
    }

    @Override
    public AttendanceDto checkingOut(AttendanceEntity attendance) {
        LocalTime defaultCheckOut = LocalTime.of(17, 30, 0);
        Optional<AttendanceEntity> currentAttendance = iAttendanceRepository.findByStudent_StudentIdAndDate(attendance.getStudent().getStudentId(), attendance.getDate());
        if (currentAttendance.isPresent()) {
            AttendanceEntity attendanceEntity = currentAttendance.get();
            attendanceEntity.setCheckOut(LocalTime.now());

            String note = attendanceEntity.getNotes() != null ? attendanceEntity.getNotes() : "";
            if (attendanceEntity.getCheckOut().isBefore(defaultCheckOut)) {
                note += ", " + Notes.leaveEarly;
            }
            attendanceEntity.setNotes(note);
            AttendanceEntity savedAttendance = iAttendanceRepository.save(attendanceEntity);
            return mapper.toDTO(savedAttendance);
        } else {
            return null;
        }
    }
}
