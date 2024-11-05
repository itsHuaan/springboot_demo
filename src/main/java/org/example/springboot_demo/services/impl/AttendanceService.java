package org.example.springboot_demo.services.impl;

import org.example.springboot_demo.dtos.AttendanceByDate;
import org.example.springboot_demo.dtos.AttendanceStatisticsDto;
import org.example.springboot_demo.dtos.AttendanceDto;
import org.example.springboot_demo.dtos.EmployeeDto;
import org.example.springboot_demo.entities.AttendanceEntity;
import org.example.springboot_demo.entities.EmployeeEntity;
import org.example.springboot_demo.mappers.impl.AttendanceMapper;
import org.example.springboot_demo.mappers.impl.EmployeeMapper;
import org.example.springboot_demo.models.Notes;
import org.example.springboot_demo.repositories.IAttendanceRepository;
import org.example.springboot_demo.repositories.IEmployeeRepository;
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
    private final IEmployeeRepository iEmployeeRepository;
    private final AttendanceMapper attendanceMapper;
    private final EmployeeMapper employeeMapper;

    @Autowired
    public AttendanceService(final IAttendanceRepository iAttendanceRepository,
                             final IEmployeeRepository iEmployeeRepository,
                             final AttendanceMapper attendanceMapper,
                             final EmployeeMapper employeeMapper) {
        this.iAttendanceRepository = iAttendanceRepository;
        this.iEmployeeRepository = iEmployeeRepository;
        this.attendanceMapper = attendanceMapper;
        this.employeeMapper = employeeMapper;
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
        long studentId = attendanceEntity.getEmployee().getEmployeeId();
        int month = attendanceEntity.getDate().getMonthValue();
        int year = attendanceEntity.getDate().getYear();
        EmployeeEntity employeeEntity = iEmployeeRepository.findById(studentId).orElse(null);
        int paidLeaves = iAttendanceRepository.countPaidLeaves(studentId, month, year);
        int unpaidLeaves = iAttendanceRepository.countUnpaidLeaves(studentId, month, year);
        if (employeeEntity != null) {
            if (unpaidLeaves > 0) {
                paidLeaves -= Math.min(unpaidLeaves, paidLeaves);
            }
            if (paidLeaves == 0) {
                employeeEntity.setUnusedPaidLeaves(employeeEntity.getUnusedPaidLeaves() + 1);
            } else {
                employeeEntity.setUnusedPaidLeaves(0);
            }
            iEmployeeRepository.save(employeeEntity);
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
        Optional<AttendanceEntity> currentAttendanceOpt = iAttendanceRepository.findByEmployee_EmployeeIdAndDate(attendance.getEmployee().getEmployeeId(), attendance.getDate());
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
                        currentAttendance.getEmployee().getEmployeeId(),
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
            EmployeeEntity employeeEntity = iEmployeeRepository.findById(studentId).orElse(null);
            if (employeeEntity != null) {
                int workingDays = iAttendanceRepository.countWorkingDays(studentId, month, year);
                int paidLeaves = iAttendanceRepository.countPaidLeaves(studentId, month, year);
                int unpaidLeaves = iAttendanceRepository.countUnpaidLeaves(studentId, month, year);
                statisticsDtos.add(new AttendanceStatisticsDto(employeeEntity.getName(), workingDays, paidLeaves, unpaidLeaves));
            }
        } else {
            statisticsDtos = iEmployeeRepository.findAll().stream()
                    .map(studentEntity -> {
                        EmployeeDto employeeDto = employeeMapper.toDTO(studentEntity);
                        int workingDays = iAttendanceRepository.countWorkingDays(employeeDto.getEmployeeId(), month, year);
                        int paidLeaves = iAttendanceRepository.countPaidLeaves(employeeDto.getEmployeeId(), month, year);
                        int unpaidLeaves = iAttendanceRepository.countUnpaidLeaves(employeeDto.getEmployeeId(), month, year);
                        return new AttendanceStatisticsDto(employeeDto.getName(), workingDays, paidLeaves, unpaidLeaves);
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
