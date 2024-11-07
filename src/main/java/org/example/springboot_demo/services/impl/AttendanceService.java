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
        return countPaidLeaveDays(employeeId, month, year) < 1;
    }

    @Override
    public List<AttendanceStatisticsDto> getStatistics(Long employeeId, int month, int year) {
        List<AttendanceStatisticsDto> statistics = new ArrayList<>();
        if (employeeId != null) {
            EmployeeEntity employeeEntity = iEmployeeRepository.findById(employeeId).orElse(null);
            if (employeeEntity != null) {
                int workingDays = (int) countWorkingDays(employeeId, month, year);
                int paidLeaveDays = (int) countPaidLeaveDays(employeeId, month, year);
                List<LocalDate> paidLeaveDayList = paidLeaveDays(employeeId, month, year);
                int unpaidLeaveDays = (int) countUnpaidLeaveDays(employeeId, month, year);
                List<LocalDate> unpaidLeaveDayList = unpaidLeaveDays(employeeId, month, year);
                int lateDays = (int) countLateDays(employeeId, month, year);
                Map<LocalDate, Long> lateDayList = lateDays(employeeId, month, year);
                int leaveEarlyDays = (int) countLeaveEarlyDays(employeeId, month, year);
                Map<LocalDate, Long> leaveEarlyDayList = leaveEarlyDays(employeeId, month, year);
                long sumLateArrivalTime = sumLateArrivalTime(employeeId, month, year);
                long sumEarlyLeavingTime = sumEarlyLeaveTime(employeeId, month, year);
                statistics.add(new AttendanceStatisticsDto(
                        employeeEntity.getName(),
                        workingDays,
                        paidLeaveDays,
                        paidLeaveDayList,
                        unpaidLeaveDays,
                        unpaidLeaveDayList,
                        lateDays,
                        lateDayList,
                        leaveEarlyDays,
                        leaveEarlyDayList,
                        sumLateArrivalTime,
                        sumEarlyLeavingTime));
            }
        } else {
            statistics = iEmployeeRepository.findAll().stream()
                    .map(employeeEntity -> {
                        EmployeeDto employeeDto = employeeMapper.toDTO(employeeEntity);
                        Long _employeeId = employeeDto.getEmployeeId();
                        int workingDays = (int) countWorkingDays(_employeeId, month, year);
                        int paidLeaveDays = (int) countPaidLeaveDays(_employeeId, month, year);
                        List<LocalDate> paidLeaveDayList = paidLeaveDays(_employeeId, month, year);
                        int unpaidLeaveDays = (int) countUnpaidLeaveDays(_employeeId, month, year);
                        List<LocalDate> unpaidLeaveDayList = unpaidLeaveDays(_employeeId, month, year);
                        int lateDays = (int) countLateDays(_employeeId, month, year);
                        Map<LocalDate, Long> lateDayList = lateDays(_employeeId, month, year);
                        int leaveEarlyDays = (int) countLeaveEarlyDays(_employeeId, month, year);
                        Map<LocalDate, Long> leaveEarlyDayList = leaveEarlyDays(_employeeId, month, year);
                        long sumLateArrivalTime = sumLateArrivalTime(_employeeId, month, year);
                        long sumEarlyLeavingTime = sumEarlyLeaveTime(_employeeId, month, year);
                        return new AttendanceStatisticsDto(
                                employeeDto.getName(),
                                workingDays,
                                paidLeaveDays,
                                paidLeaveDayList,
                                unpaidLeaveDays,
                                unpaidLeaveDayList,
                                lateDays,
                                lateDayList,
                                leaveEarlyDays,
                                leaveEarlyDayList,
                                sumLateArrivalTime,
                                sumEarlyLeavingTime);
                    }).toList();
        }
        return statistics;
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

    private long countWorkingDays(Long employeeId, int month, int year) {
        return iAttendanceRepository.findDistinctAttendances(employeeId, month, year).stream()
                .filter(attendance -> attendance.getCheckIn() != null
                        && attendance.getCheckOut() != null
                        && attendance.getCheckInStatus() != null
                        && !attendance.getCheckInStatus().equals(AttendanceStatus.absent.toString()))
                .count();
    }

    private long countPaidLeaveDays(Long employeeId, int month, int year) {
        return iAttendanceRepository.findDistinctAttendances(employeeId, month, year).stream()
                .filter(attendance -> attendance.getCheckInStatus() != null
                        && attendance.getCheckInStatus().equals(AttendanceStatus.absent.toString())
                        && attendance.isPaidLeave())
                .count();
    }

    private List<LocalDate> paidLeaveDays(Long employeeId, int month, int year) {
        return iAttendanceRepository.findDistinctAttendances(employeeId, month, year).stream()
                .filter(attendance -> attendance.getCheckInStatus() != null
                        && attendance.getCheckInStatus().equals(AttendanceStatus.absent.toString())
                        && attendance.isPaidLeave())
                .map(AttendanceEntity::getDate).collect(Collectors.toList());
    }

    private long countUnpaidLeaveDays(Long employeeId, int month, int year) {
        return iAttendanceRepository.findDistinctAttendances(employeeId, month, year).stream()
                .filter(attendance -> attendance.getCheckInStatus() != null
                        && attendance.getCheckInStatus().equals(AttendanceStatus.absent.toString())
                        && !attendance.isPaidLeave())
                .count();
    }

    private List<LocalDate> unpaidLeaveDays(Long employeeId, int month, int year) {
        return iAttendanceRepository.findDistinctAttendances(employeeId, month, year).stream()
                .filter(attendance -> attendance.getCheckInStatus() != null
                        && attendance.getCheckInStatus().equals(AttendanceStatus.absent.toString())
                        && !attendance.isPaidLeave())
                .map(AttendanceEntity::getDate).collect(Collectors.toList());
    }

    private long countLateDays(Long employeeId, int month, int year) {
        return iAttendanceRepository.findDistinctAttendances(employeeId, month, year).stream()
                .filter(attendance -> attendance.getCheckIn() != null
                        && attendance.getCheckOut() != null
                        && attendance.getCheckInStatus() != null
                        && attendance.getCheckInStatus().equals(AttendanceStatus.lateArrival.toString()))
                .count();
    }

    private Map<LocalDate, Long> lateDays(Long employeeId, int month, int year) {
        return iAttendanceRepository.findDistinctAttendances(employeeId, month, year).stream()
                .filter(attendance -> attendance.getCheckIn() != null
                        && attendance.getCheckInStatus() != null
                        && ((attendance.getCheckIn().isAfter(LocalTime.of(8, 30))
                        && attendance.getCheckIn().isBefore(LocalTime.of(9, 0)))
                        || (attendance.getCheckIn().isAfter(LocalTime.of(13, 0))
                        && attendance.getCheckIn().isBefore(LocalTime.of(13, 30))))
                        && attendance.getCheckInStatus().equalsIgnoreCase(AttendanceStatus.lateArrival.toString()))
                .collect(Collectors.toMap(
                        AttendanceEntity::getDate,
                        attendance -> {
                            LocalTime checkIn = attendance.getCheckIn();
                            if (checkIn.isAfter(LocalTime.of(8, 30)) && checkIn.isBefore(LocalTime.of(9, 0))) {
                                return Duration.between(LocalTime.of(8, 30), checkIn).toMinutes();
                            } else if (checkIn.isAfter(LocalTime.of(13, 0)) && checkIn.isBefore(LocalTime.of(13, 30))) {
                                return Duration.between(LocalTime.of(13, 0), checkIn).toMinutes();
                            } else {
                                return 0L;
                            }
                        },
                        Long::sum
                ));
    }

    private long countLeaveEarlyDays(Long employeeId, int month, int year) {
        return iAttendanceRepository.findDistinctAttendances(employeeId, month, year).stream()
                .filter(attendance -> attendance.getCheckIn() != null
                        && attendance.getCheckOut() != null
                        && attendance.getCheckOutStatus() != null
                        && attendance.getCheckOutStatus().equals(AttendanceStatus.leaveEarly.toString()))
                .count();
    }

    private Map<LocalDate, Long> leaveEarlyDays(Long employeeId, int month, int year) {
        return iAttendanceRepository.findDistinctAttendances(employeeId, month, year).stream()
                .filter(attendance -> attendance.getCheckIn() != null
                        && attendance.getCheckOut() != null
                        && attendance.getCheckOutStatus() != null
                        && attendance.getCheckOutStatus().equalsIgnoreCase(AttendanceStatus.leaveEarly.toString())
                        && attendance.getCheckOut().isBefore(LocalTime.of(17, 0)))
                .collect(Collectors.groupingBy(
                        AttendanceEntity::getDate,
                        Collectors.summingLong(attendance -> Duration.between(attendance.getCheckOut(), LocalTime.of(17, 0)).toMinutes())
                ));
    }

    private long sumLateArrivalTime(Long employeeId, int month, int year) {
        return iAttendanceRepository.findDistinctAttendances(employeeId, month, year).stream()
                .filter(attendance -> attendance.getCheckIn() != null)
                .mapToLong(attendance -> {
                    LocalTime checkIn = attendance.getCheckIn();
                    return checkIn.isAfter(LocalTime.of(8, 30)) && checkIn.isBefore(LocalTime.of(9, 0))
                            ? Duration.between(LocalTime.of(8, 30), checkIn).toMinutes()
                            : 0;
                }).sum();
    }

    private long sumEarlyLeaveTime(Long employeeId, int month, int year) {
        return iAttendanceRepository.findDistinctAttendances(employeeId, month, year).stream()
                .filter(attendance -> attendance.getCheckOut() != null)
                .mapToLong(attendance -> {
                    LocalTime checkOut = attendance.getCheckOut();
                    return attendance.getCheckOutStatus().equalsIgnoreCase("leaveEarly") && checkOut.isBefore(LocalTime.of(17, 0))
                            ? Duration.between(checkOut, LocalTime.of(17, 0)).toMinutes()
                            : 0;
                }).sum();
    }
}
