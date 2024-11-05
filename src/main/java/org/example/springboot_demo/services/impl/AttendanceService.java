package org.example.springboot_demo.services.impl;

import org.example.springboot_demo.dtos.AttendanceByDate;
import org.example.springboot_demo.dtos.AttendanceStatisticsDto;
import org.example.springboot_demo.dtos.AttendanceDto;
import org.example.springboot_demo.dtos.StudentDto;
import org.example.springboot_demo.entities.AttendanceEntity;
import org.example.springboot_demo.entities.StudentEntity;
import org.example.springboot_demo.mappers.impl.AttendanceMapper;
import org.example.springboot_demo.mappers.impl.StudentMapper;
import org.example.springboot_demo.models.Notes;
import org.example.springboot_demo.repositories.IAttendanceRepository;
import org.example.springboot_demo.repositories.IStudentRepository;
import org.example.springboot_demo.services.IAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceService implements IAttendanceService {

    private final IAttendanceRepository iAttendanceRepository;
    private final IStudentRepository iStudentRepository;
    private final AttendanceMapper attendanceMapper;
    private final StudentMapper studentMapper;

    @Autowired
    public AttendanceService(final IAttendanceRepository iAttendanceRepository,
                             final IStudentRepository iStudentRepository,
                             final AttendanceMapper attendanceMapper,
                             final StudentMapper studentMapper) {
        this.iAttendanceRepository = iAttendanceRepository;
        this.iStudentRepository = iStudentRepository;
        this.attendanceMapper = attendanceMapper;
        this.studentMapper = studentMapper;
    }

    @Override
    public List<AttendanceDto> findAll() {
        return iAttendanceRepository.findAll().stream().map(attendanceMapper::toDTO).toList();
    }

    @Override
    public AttendanceDto findById(Long id) {
        return attendanceMapper.toDTO(Objects.requireNonNull(iAttendanceRepository.findById(id).orElse(null)));
    }

    @Override
    public AttendanceDto save(AttendanceEntity attendanceEntity) {
        long studentId = attendanceEntity.getStudent().getStudentId();
        int month = attendanceEntity.getDate().getMonthValue();
        int year = attendanceEntity.getDate().getYear();
        StudentEntity studentEntity = iStudentRepository.findById(studentId).orElse(null);
        int paidLeaves = iAttendanceRepository.countPaidLeaves(studentId, month, year);
        int unpaidLeaves = iAttendanceRepository.countUnpaidLeaves(studentId, month, year);
        if (studentEntity != null) {
            if (unpaidLeaves > 0) {
                paidLeaves -= Math.min(unpaidLeaves, paidLeaves);
            }
            if (paidLeaves == 0) {
                studentEntity.setUnusedPaidLeaves(studentEntity.getUnusedPaidLeaves() + 1);
            } else {
                studentEntity.setUnusedPaidLeaves(0);
            }
            iStudentRepository.save(studentEntity);
        }
        return attendanceMapper.toDTO(iAttendanceRepository.save(attendanceEntity));
    }

    @Override
    public int delete(Long aLong) {
        return 0;
    }

    @Override
    public List<AttendanceDto> getByDate(LocalDate date) {
        return iAttendanceRepository.findByDate(date).stream().map(attendanceMapper::toDTO).toList();
    }

    @Override
    public AttendanceDto checkingOut(AttendanceEntity attendance) {
        LocalTime defaultCheckOut = LocalTime.of(17, 30, 0);
        Optional<AttendanceEntity> currentAttendanceOpt = iAttendanceRepository.findByStudent_StudentIdAndDate(attendance.getStudent().getStudentId(), attendance.getDate());
        if (currentAttendanceOpt.isPresent()) {
            AttendanceEntity currentAttendance = currentAttendanceOpt.get();
            currentAttendance.setCheckOut(LocalTime.now());

            String checkOutStatus = currentAttendance.getCheckOutStatus() != null
                    ? currentAttendance.getCheckOutStatus()
                    : "";
            if (currentAttendance.getCheckOut().isBefore(defaultCheckOut)) {
                checkOutStatus += Notes.leaveEarly.toString();
            }
            if ("absent".equalsIgnoreCase(currentAttendance.getCheckOutStatus())) {
                currentAttendance.setPaidLeave(canGrantPaidLeave(
                        currentAttendance.getStudent().getStudentId(),
                        currentAttendance.getDate().getMonthValue(),
                        currentAttendance.getDate().getYear()
                ));
            }
            currentAttendance.setCheckOutStatus(checkOutStatus);
            return attendanceMapper.toDTO(iAttendanceRepository.save(currentAttendance));
        }
        return null;
    }

    @Override
    public boolean canGrantPaidLeave(Long studentId, int month, int year) {
        return iAttendanceRepository.countPaidLeaves(studentId, month, year) < 1;
    }

    @Override
    public List<AttendanceStatisticsDto> getStatistics(Long studentId, int month, int year) {
        List<AttendanceStatisticsDto> statisticsDtos = new ArrayList<>();
        if (studentId != null) {
            StudentEntity studentEntity = iStudentRepository.findById(studentId).orElse(null);
            if (studentEntity != null) {
                int workingDays = iAttendanceRepository.countWorkingDays(studentId, month, year);
                int paidLeaves = iAttendanceRepository.countPaidLeaves(studentId, month, year);
                int unpaidLeaves = iAttendanceRepository.countUnpaidLeaves(studentId, month, year);
                statisticsDtos.add(new AttendanceStatisticsDto(studentEntity.getName(), workingDays, paidLeaves, unpaidLeaves));
            }
        } else {
            statisticsDtos = iStudentRepository.findAll().stream()
                    .map(studentEntity -> {
                        StudentDto studentDto = studentMapper.toDTO(studentEntity);

                        int workingDays = iAttendanceRepository.countWorkingDays(studentDto.getStudentId(), month, year);
                        int paidLeaves = iAttendanceRepository.countPaidLeaves(studentDto.getStudentId(), month, year);
                        int unpaidLeaves = iAttendanceRepository.countUnpaidLeaves(studentDto.getStudentId(), month, year);
                        return new AttendanceStatisticsDto(studentDto.getName(), workingDays, paidLeaves, unpaidLeaves);
                    }).toList();
        }
        return statisticsDtos;
    }

    @Override
    public List<AttendanceByDate> getAttendanceGroupByDate() {
        List<AttendanceEntity> entities = iAttendanceRepository.findAll();
        Map<LocalDate, List<AttendanceDto>> attendanceByDate = entities.stream()
                .collect(Collectors.groupingBy(
                        AttendanceEntity::getDate,
                        Collectors.mapping(attendanceMapper::toDTO, Collectors.toList())
                ));
        return attendanceByDate.entrySet().stream()
                .map(entry -> AttendanceByDate.builder()
                        .date(entry.getKey())
                        .attendances(entry.getValue())
                        .build()).toList();
    }
}
