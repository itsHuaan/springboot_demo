package org.example.springboot_demo.services.impl;

import org.example.springboot_demo.dtos.AttendanceByDate;
import org.example.springboot_demo.dtos.AttendanceStatisticsDto;
import org.example.springboot_demo.dtos.AttendanceDto;
import org.example.springboot_demo.entities.AttendanceEntity;
import org.example.springboot_demo.entities.EmployeeEntity;
import org.example.springboot_demo.mappers.impl.AttendanceMapper;
import org.example.springboot_demo.models.AttendanceStatus;
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

    @Autowired
    public AttendanceService(final IAttendanceRepository iAttendanceRepository,
                             final IEmployeeRepository iEmployeeRepository,
                             final AttendanceMapper attendanceMapper) {
        this.iAttendanceRepository = iAttendanceRepository;
        this.iEmployeeRepository = iEmployeeRepository;
        this.attendanceMapper = attendanceMapper;
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
        long employeeId = attendanceEntity.getEmployee().getEmployeeId();
        LocalDate today = attendanceEntity.getDate(); // Giả sử attendanceEntity đã có ngày
        int month = today.getMonthValue();
        int year = today.getYear();
        LocalTime currentCheckInTime = LocalTime.now();
        Optional<AttendanceEntity> existingAttendance = iAttendanceRepository.findByEmployee_EmployeeIdAndDate(employeeId, today);
        if (existingAttendance.isPresent()) {
            AttendanceEntity attendanceToUpdate = existingAttendance.get();

            if (attendanceToUpdate.getCheckInTimes() == null) {
                attendanceToUpdate.setCheckInTimes(new ArrayList<>());
            }
            attendanceToUpdate.getCheckInTimes().add(currentCheckInTime);

            if (currentCheckInTime.isAfter(LocalTime.of(8, 30))) {
                attendanceToUpdate.setCheckInStatus(AttendanceStatus.lateArrival.toString());
            } else {
                attendanceToUpdate.setCheckInStatus(AttendanceStatus.onTime.toString());
            }

            return attendanceMapper.toDTO(iAttendanceRepository.save(attendanceToUpdate));
        } else {
            attendanceEntity.setCheckInTimes(new ArrayList<>());
            attendanceEntity.getCheckInTimes().add(currentCheckInTime);

            if (currentCheckInTime.isAfter(LocalTime.of(8, 30))) {
                attendanceEntity.setCheckInStatus(AttendanceStatus.lateArrival.toString());
            } else {
                attendanceEntity.setCheckInStatus(AttendanceStatus.onTime.toString());
            }
            return attendanceMapper.toDTO(iAttendanceRepository.save(attendanceEntity));
        }
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
                checkOutStatus += AttendanceStatus.leaveEarly.toString();
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
    public boolean canGrantPaidLeave(Long employeeId, int month, int year) {
        return iAttendanceRepository.countPaidLeaves(employeeId, month, year) < 1;
    }

    @Override
    public List<AttendanceStatisticsDto> getStatistics(Long employeeId, int month, int year) {
        List<AttendanceStatisticsDto> statisticsDtos = new ArrayList<>();
        List<EmployeeEntity> employees = iEmployeeRepository.findAll();
        for (EmployeeEntity employee : employees) {
            int workingDays = iAttendanceRepository.countWorkingDays(employeeId, month, year);
            int paidLeaves = iAttendanceRepository.countPaidLeaves(employeeId, month, year);
            int unpaidLeaves = iAttendanceRepository.countUnpaidLeaves(employeeId, month, year);

            int lateDays = iAttendanceRepository.countLateDays(employeeId, month, year);
            long totalLateTime = iAttendanceRepository.sumLateMinutes(employeeId, month, year);

            int overtimeDays = iAttendanceRepository.countOvertimeDays(employeeId, month, year);
            long totalOvertimeMinutes = iAttendanceRepository.sumOvertimeMinutes(employeeId, month, year);

            AttendanceStatisticsDto dto = AttendanceStatisticsDto.builder()
                    .name(employee.getName())
                    .workingDays(workingDays)
                    .paidLeaveDays(paidLeaves)
                    .unpaidLeaveDays(unpaidLeaves)
                    .lateDays(lateDays)
                    .totalLateTime(totalLateTime)
                    .overtimeDays(overtimeDays)
                    .totalOvertimeMinutes(totalOvertimeMinutes)
                    .build();

            statisticsDtos.add(dto);
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
