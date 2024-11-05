package org.example.springboot_demo.repositories;

import org.example.springboot_demo.entities.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface IAttendanceRepository extends JpaRepository<AttendanceEntity, Long> {
    List<AttendanceEntity> findByDate(LocalDate Date);

    Optional<AttendanceEntity> findByEmployee_EmployeeIdAndDate(long employee_employeeId, LocalDate date);

    @Query("select count(a) " +
            "from AttendanceEntity a " +
            "where a.employee.employeeId = :employeeId " +
            "and month(a.date) = :month " +
            "and year(a.date) = :year " +
            "and a.checkInStatus <> 'absent'")
    int countWorkingDays(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);

    @Query("select count(a) " +
            "from AttendanceEntity a " +
            "where a.employee.employeeId = :employeeId " +
            "and month(a.date) = :month " +
            "and year(a.date) = :year " +
            "and a.isPaidLeave = true")
    int countPaidLeaves(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);


    @Query("select count(a) " +
            "from AttendanceEntity a " +
            "where a.employee.employeeId = :employeeId " +
            "and month(a.date) = :month " +
            "and year(a.date) = :year " +
            "and a.checkInStatus = 'absent'" +
            "and a.isPaidLeave = false")
    int countUnpaidLeaves(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(a) FROM AttendanceEntity a WHERE a.employee.employeeId = :employeeId AND MONTH(a.date) = :month AND YEAR(a.date) = :year AND a.checkInStatus = 'late'")
    int countLateDays(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);


    @Query("SELECT a FROM AttendanceEntity a WHERE a.employee.employeeId = :employeeId AND MONTH(a.date) = :month AND YEAR(a.date) = :year AND a.checkInStatus = 'late'")
    long sumLateMinutes(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);


    @Query("select count(a) from AttendanceEntity a where a.employee.employeeId = :employeeId and month(a.date) = :month and year(a.date) = :year and a.isOvertime = true")
    int countOvertimeDays(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);

    @Query("SELECT a FROM AttendanceEntity a " +
            "WHERE a.employee.employeeId = :employeeId " +
            "AND month(a.date) = :month " +
            "AND year(a.date) = :year " +
            "AND a.isOvertime = true")
    long sumOvertimeMinutes(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);

}
