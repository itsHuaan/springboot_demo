package org.example.springboot_demo.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceStatisticsDto {
    private String name;
    private int workingDays;
    private int paidLeaveDays;
    private int unpaidLeaveDays;
    private int lateDays;
    private int leaveEarlyDays;
    private long lateArrivalTime;
    private long earlyLeavingTime;
}
