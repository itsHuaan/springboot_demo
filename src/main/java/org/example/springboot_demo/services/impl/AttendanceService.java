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
import org.example.springboot_demo.utils.specifications.AttendanceSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.springboot_demo.utils.specifications.AttendanceSpecifications.*;

@Service
public class AttendanceService implements IAttendanceService {

    private final IAttendanceRepository iAttendanceRepository;
    private final IEmployeeRepository iEmployeeRepository;
    private final AttendanceMapper attendanceMapper;
    private final EmployeeMapper employeeMapper;
    private Specification<AttendanceEntity> specification = null;

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
        return iAttendanceRepository.findAll(hasLatestCheckOut().and(hasDate(date))).stream().map(attendanceMapper::toDTO).toList();
    }

    @Override
    public boolean canGrantPaidLeave(Long employeeId, int month, int year) {
        specification = Specification
                .where(AttendanceSpecifications.hasEmployeeId(employeeId))
                .and(AttendanceSpecifications.hasMonth(month))
                .and(AttendanceSpecifications.hasYear(year))
                .and(hasLatestCheckOut());
        return getPaidLeaveDays(specification).isEmpty();
    }

    @Override
    public List<AttendanceStatisticsDto> getStatistics(Long employeeId, int month, int year) {
        List<AttendanceStatisticsDto> statistics = new ArrayList<>();
        if (employeeId != null) {
            EmployeeEntity employeeEntity = iEmployeeRepository.findById(employeeId).orElse(null);
            if (employeeEntity != null) {
                specification = Specification
                        .where(hasEmployeeId(employeeId))
                        .and(hasMonth(month))
                        .and(hasYear(year))
                        .and(hasLatestCheckOut());
                int workingDays = (int) countWorkingDays(specification);
                int paidLeaveDays = getPaidLeaveDays(specification).size();
                List<LocalDate> paidLeaveDayList = getPaidLeaveDays(specification);
                int unpaidLeaveDays = getUnpaidLeaveDays(specification).size();
                List<LocalDate> unpaidLeaveDayList = getUnpaidLeaveDays(specification);
                int lateDays = getLateDayList(specification).size();
                Map<LocalDate, Long> lateDayList = getLateDayList(specification);
                int leaveEarlyDays = getLeaveEarlyDayList(specification).size();
                Map<LocalDate, Long> leaveEarlyDayList = getLeaveEarlyDayList(specification);
                Map<LocalDate, Long> frequencyOfCheckingOut = getFrequencyOfCheckingOut(hasEmployeeId(employeeId));
                long sumLateArrivalTime = getSumLateArrivalTime(specification);
                long sumEarlyLeavingTime = getSumEarlyLeaveTime(specification);
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
                        frequencyOfCheckingOut,
                        sumLateArrivalTime,
                        sumEarlyLeavingTime));
            }
        } else {
            statistics = iEmployeeRepository.findAll().stream()
                    .map(employeeEntity -> {
                        EmployeeDto employeeDto = employeeMapper.toDTO(employeeEntity);
                        Long _employeeId = employeeDto.getEmployeeId();
                        specification = Specification
                                .where(hasEmployeeId(_employeeId))
                                .and(hasMonth(month))
                                .and(hasYear(year))
                                .and(hasLatestCheckOut());
                        int workingDays = (int) countWorkingDays(specification);
                        int paidLeaveDays = getPaidLeaveDays(specification).size();
                        List<LocalDate> paidLeaveDayList = getPaidLeaveDays(specification);
                        int unpaidLeaveDays = getUnpaidLeaveDays(specification).size();
                        List<LocalDate> unpaidLeaveDayList = getUnpaidLeaveDays(specification);
                        int lateDays = getLateDayList(specification).size();
                        Map<LocalDate, Long> lateDayList = getLateDayList(specification);
                        int leaveEarlyDays = getLeaveEarlyDayList(specification).size();
                        Map<LocalDate, Long> leaveEarlyDayList = getLeaveEarlyDayList(specification);
                        Map<LocalDate, Long> frequencyOfCheckingOut = getFrequencyOfCheckingOut(hasEmployeeId(_employeeId));
                        long sumLateArrivalTime = getSumLateArrivalTime(specification);
                        long sumEarlyLeavingTime = getSumEarlyLeaveTime(specification);
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
                                frequencyOfCheckingOut,
                                sumLateArrivalTime,
                                sumEarlyLeavingTime);
                    }).toList();
        }
        return statistics;
    }

    @Override
    public List<AttendanceByDate> getAttendanceGroupByDate() {
        List<AttendanceEntity> entities = iAttendanceRepository.findAll(hasLatestCheckOut());
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

    @Override
    public List<AttendanceDto> getByEmployeeId(Long employeeId) {
        specification = Specification.where(hasEmployeeId(employeeId));
        return iAttendanceRepository.findAll(specification).stream().map(attendanceMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDto> getByEmployeeIdAndDate(Long employeeId, LocalDate date) {
        specification = Specification.where(hasEmployeeId(employeeId).and(hasDate(date)));
        return iAttendanceRepository.findAll(specification).stream().map(attendanceMapper::toDTO).collect(Collectors.toList());
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

    private long countWorkingDays(Specification<AttendanceEntity> specification) {
        return iAttendanceRepository.findAll(specification).stream()
                .filter(attendance -> attendance.getCheckIn() != null
                        && attendance.getCheckOut() != null
                        && attendance.getCheckInStatus() != null
                        && !attendance.getCheckInStatus().equalsIgnoreCase(AttendanceStatus.absent.toString()))
                .count();
    }

    private List<LocalDate> getPaidLeaveDays(Specification<AttendanceEntity> specification) {
        return iAttendanceRepository.findAll(specification).stream()
                .filter(attendance -> attendance.getCheckInStatus() != null
                        && attendance.getCheckInStatus().equals(AttendanceStatus.absent.toString())
                        && attendance.isPaidLeave())
                .map(AttendanceEntity::getDate).collect(Collectors.toList());
    }

    private List<LocalDate> getUnpaidLeaveDays(Specification<AttendanceEntity> specification) {
        return iAttendanceRepository.findAll(specification).stream()
                .filter(attendance -> attendance.getCheckInStatus() != null
                        && attendance.getCheckInStatus().equals(AttendanceStatus.absent.toString())
                        && !attendance.isPaidLeave())
                .map(AttendanceEntity::getDate).collect(Collectors.toList());
    }

    private Map<LocalDate, Long> getLateDayList(Specification<AttendanceEntity> specification) {
        return iAttendanceRepository.findAll(specification).stream()
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

    private Map<LocalDate, Long> getLeaveEarlyDayList(Specification<AttendanceEntity> specification) {
        return iAttendanceRepository.findAll(specification).stream()
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

    private Map<LocalDate, Long> getFrequencyOfCheckingOut(Specification<AttendanceEntity> specification) {
        return iAttendanceRepository.findAll(specification).stream()
                .filter(attendance -> attendance.getCheckOut() != null)
                .collect(Collectors.groupingBy(
                        AttendanceEntity::getDate,
                        Collectors.counting()
                ));
    }

    private long getSumLateArrivalTime(Specification<AttendanceEntity> specification) {
        return iAttendanceRepository.findAll(specification).stream()
                .filter(attendance -> attendance.getCheckIn() != null)
                .mapToLong(attendance -> {
                    LocalTime checkIn = attendance.getCheckIn();
                    return checkIn.isAfter(LocalTime.of(8, 30)) && checkIn.isBefore(LocalTime.of(9, 0))
                            ? Duration.between(LocalTime.of(8, 30), checkIn).toMinutes()
                            : 0;
                }).sum();
    }

    private long getSumEarlyLeaveTime(Specification<AttendanceEntity> specification) {
        return iAttendanceRepository.findAll(specification).stream()
                .filter(attendance -> attendance.getCheckOut() != null)
                .mapToLong(attendance -> {
                    LocalTime checkOut = attendance.getCheckOut();
                    return attendance.getCheckOutStatus().equalsIgnoreCase(AttendanceStatus.leaveEarly.toString()) && checkOut.isBefore(LocalTime.of(17, 0))
                            ? Duration.between(checkOut, LocalTime.of(17, 0)).toMinutes()
                            : 0;
                }).sum();
    }
}
