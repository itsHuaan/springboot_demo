package org.example.springboot_demo.dtos;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceStatisticsDto {
    private int month;
    private int year;
    private long employeeId;
    private String name;
    private int workingDays;
    private int paidLeaveDays;
    private List<LocalDate> paidLeaveDayList;
    private int unpaidLeaveDays;
    private List<LocalDate> unpaidLeaveDayList;
    private int lateDays;
    private Map<LocalDate, Long> lateDayList;
    private int leaveEarlyDays;
    private Map<LocalDate, Long> leaveEarlyDayList;
    private Map<LocalDate, Long> frequencyOfCheckingOut;
    private long sumLateArrivalTime;
    private long sumEarlyLeavingTime;
}
