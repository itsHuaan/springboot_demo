package org.example.springboot_demo.services.impl;

import org.example.springboot_demo.dtos.AttendanceByDate;
import org.example.springboot_demo.dtos.AttendanceStatisticsDto;
import org.example.springboot_demo.dtos.AttendanceDto;
import org.example.springboot_demo.dtos.EmployeeDto;
import org.example.springboot_demo.entities.AttendanceEntity;
import org.example.springboot_demo.entities.EmployeeEntity;
import org.example.springboot_demo.mappers.impl.AttendanceMapper;
import org.example.springboot_demo.mappers.impl.EmployeeMapper;
import org.example.springboot_demo.models.AttendanceStatus;
import org.example.springboot_demo.repositories.IAttendanceRepository;
import org.example.springboot_demo.repositories.IEmployeeRepository;
import org.example.springboot_demo.services.IAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
        long employeeId = attendanceEntity.getEmployee().getEmployeeId();
        LocalDate date = attendanceEntity.getDate();

        Optional<AttendanceEntity> existingRecord = iAttendanceRepository.findFirstByEmployee_EmployeeIdAndDate(employeeId, date);
        if (existingRecord.isPresent()) {
            AttendanceEntity firstRecord = existingRecord.get();
            attendanceEntity.setCheckIn(firstRecord.getCheckIn());
            attendanceEntity.setCheckInStatus(firstRecord.getCheckInStatus());
            attendanceEntity.setPaidLeave(firstRecord.isPaidLeave());

            LocalTime checkOutTime = attendanceEntity.getCheckOut() != null
                    ? attendanceEntity.getCheckOut()
                    : LocalTime.now();
            String checkOutStatus = handleCheckOutStatus(checkOutTime);
            attendanceEntity.setCheckOut(checkOutTime);
            attendanceEntity.setCheckOutStatus(checkOutStatus);
        } else {
            LocalTime checkInTime = attendanceEntity.getCheckIn() != null
                    ? attendanceEntity.getCheckIn()
                    : LocalTime.now();
            String checkInStatus = handleCheckInStatus(checkInTime);
            attendanceEntity.setNotes(
                    checkInStatus.equalsIgnoreCase("onTime") && checkInTime.isAfter(LocalTime.of(12, 0))
                            ? "Half day working"
                            : null
            );
            handleLeaveDays(attendanceEntity, employeeId, checkInStatus);
            attendanceEntity.setCheckInStatus(checkInStatus);
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
    public boolean canGrantPaidLeave(Long employeeId, int month, int year) {
        return iAttendanceRepository.countPaidLeaves(employeeId, month, year) < 1;
    }

    @Override
    public List<AttendanceStatisticsDto> getStatistics(Long employeeId, int month, int year) {
        List<AttendanceStatisticsDto> statisticsDtos = new ArrayList<>();
        if (employeeId != null) {
            EmployeeEntity employeeEntity = iEmployeeRepository.findById(employeeId).orElse(null);
            if (employeeEntity != null) {
                LocalDate currentDate = LocalDate.now();
                LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
                int totalDays = (int) firstDayOfMonth.datesUntil(currentDate.plusDays(1)).count();
                int workingDays = totalDays - (iAttendanceRepository.countPaidLeaves(employeeId, month, year)
                        + iAttendanceRepository.countUnpaidLeaves(employeeId, month, year));
                int paidLeaves = iAttendanceRepository.countPaidLeaves(employeeId, month, year);
                int unpaidLeaves = iAttendanceRepository.countUnpaidLeaves(employeeId, month, year);
                int lateDays = iAttendanceRepository.countLateArrivals(employeeId, month, year);
                int leaveEarlyDays = iAttendanceRepository.countLeaveEarly(employeeId, month, year);
                long lateArrivalTime = sumLateArrivalTime(employeeId, month, year);
                long earlyLeavingTime = sumEarlyLeaveTime(employeeId, month, year);
                statisticsDtos.add(new AttendanceStatisticsDto(employeeEntity.getName(), workingDays, paidLeaves, unpaidLeaves, lateDays, leaveEarlyDays, lateArrivalTime, earlyLeavingTime));
            }
        } else {
            statisticsDtos = iEmployeeRepository.findAll().stream()
                    .map(employeeEntity -> {
                        EmployeeDto employeeDto = employeeMapper.toDTO(employeeEntity);
                        LocalDate currentDate = LocalDate.now();
                        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
                        int totalDays = (int) firstDayOfMonth.datesUntil(currentDate.plusDays(1)).count();
                        int workingDays = totalDays - (iAttendanceRepository.countPaidLeaves(employeeDto.getEmployeeId(), month, year)
                                + iAttendanceRepository.countUnpaidLeaves(employeeDto.getEmployeeId(), month, year));
                        int paidLeaves = iAttendanceRepository.countPaidLeaves(employeeDto.getEmployeeId(), month, year);
                        int unpaidLeaves = iAttendanceRepository.countUnpaidLeaves(employeeDto.getEmployeeId(), month, year);
                        int lateDays = iAttendanceRepository.countLateArrivals(employeeDto.getEmployeeId(), month, year);
                        int leaveEarlyDays = iAttendanceRepository.countLeaveEarly(employeeDto.getEmployeeId(), month, year);
                        long lateArrivalTime = sumLateArrivalTime(employeeDto.getEmployeeId(), month, year);
                        long earlyLeavingTime = sumEarlyLeaveTime(employeeDto.getEmployeeId(), month, year);
                        return new AttendanceStatisticsDto(employeeDto.getName(), workingDays, paidLeaves, unpaidLeaves, lateDays, leaveEarlyDays, lateArrivalTime, earlyLeavingTime);
                    }).toList();
        }
        return statisticsDtos;
    }

    @Override
    public List<AttendanceByDate> getAttendanceGroupByDate() {
        List<AttendanceEntity> entities = iAttendanceRepository.findLastRecordByCheckOut();
        Map<LocalDate, List<AttendanceDto>> attendanceByDate = entities.stream()
                .collect(Collectors.groupingBy(
                        AttendanceEntity::getDate,
                        Collectors.mapping(attendanceMapper::toDTO, Collectors.toList())
                ));
        return attendanceByDate.entrySet().stream()
                .map(entry -> AttendanceByDate.builder()
                        .date(entry.getKey())
                        .attendances(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    private void handleLeaveDays(AttendanceEntity attendanceEntity, long employeeId, String checkInStatus) {
        EmployeeEntity employeeEntity = iEmployeeRepository.findById(employeeId).orElse(null);
        if (employeeEntity != null) {
            if (checkInStatus.equalsIgnoreCase(AttendanceStatus.absent.toString())) {
                if (employeeEntity.getUnusedPaidLeaves() > 0) {
                    attendanceEntity.setPaidLeave(true);
                    employeeEntity.setUnusedPaidLeaves(employeeEntity.getUnusedPaidLeaves() - 1);
                    iEmployeeRepository.save(employeeEntity);
                } else {
                    attendanceEntity.setPaidLeave(false);
                }
            }
        }
    }

    private String handleCheckInStatus(LocalTime checkInTime) {
        if (checkInTime != null) {
            if (checkInTime.isBefore(LocalTime.of(8, 30))) {
                return AttendanceStatus.onTime.toString();
            } else if (checkInTime.isBefore(LocalTime.of(9, 0))) {
                return AttendanceStatus.lateArrival.toString();
            } else if (checkInTime.isBefore(LocalTime.of(12, 0))) {
                return AttendanceStatus.absent.toString();
            } else if (checkInTime.isBefore(LocalTime.of(13, 0))) {
                return AttendanceStatus.onTime.toString();
            } else if (checkInTime.isBefore(LocalTime.of(13, 30))) {
                return AttendanceStatus.lateArrival.toString();
            } else if (checkInTime.isBefore(LocalTime.of(16, 0))) {
                return AttendanceStatus.absent.toString();
            } else {
                return AttendanceStatus.absent.toString();
            }
        } else {
            return AttendanceStatus.absent.toString();
        }
    }

    private String handleCheckOutStatus(LocalTime checkOutTime) {
        if (checkOutTime != null) {
            if (checkOutTime.isBefore(LocalTime.of(16, 45))) {
                return AttendanceStatus.leaveEarly.toString();
            } else {
                return AttendanceStatus.onTime.toString();
            }
        } else {
            return null;
        }
    }

    private long sumLateArrivalTime(Long employeeId, int month, int year) {
        return iAttendanceRepository.findLastCheckOut(employeeId, month, year).stream()
                .filter(attendance -> attendance.getCheckIn() != null)
                .mapToLong(attendance -> {
                    LocalTime checkIn = attendance.getCheckIn();
                    return checkIn.isAfter(LocalTime.of(8, 30)) && checkIn.isBefore(LocalTime.of(9, 0))
                            ? Duration.between(LocalTime.of(8, 30), checkIn).toMinutes()
                            : 0;
                }).sum();
    }

    private long sumEarlyLeaveTime(Long employeeId, int month, int year) {
        return iAttendanceRepository.findLastCheckOut(employeeId, month, year).stream()
                .filter(attendance -> attendance.getCheckOut() != null)
                .mapToLong(attendance -> {
                    LocalTime checkOut = attendance.getCheckOut();
                    return attendance.getCheckOutStatus().equalsIgnoreCase("leaveEarly") && checkOut.isBefore(LocalTime.of(17, 0))
                            ? Duration.between(checkOut, LocalTime.of(17, 0)).toMinutes()
                            : 0;
                }).sum();
    }
}
