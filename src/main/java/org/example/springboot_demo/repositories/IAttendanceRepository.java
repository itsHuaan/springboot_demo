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
            "where a.employee.employeeId = :studentId " +
            "and month(a.date) = :month " +
            "and year(a.date) = :year " +
            "and a.checkInStatus <> 'absent'")
    int countWorkingDays(@Param("studentId") Long studentId, @Param("month") int month, @Param("year") int year);

    @Query("select count(a) " +
            "from AttendanceEntity a " +
            "where a.employee.employeeId = :studentId " +
            "and month(a.date) = :month " +
            "and year(a.date) = :year " +
            "and a.isPaidLeave = true")
    int countPaidLeaves(@Param("studentId") Long studentId, @Param("month") int month, @Param("year") int year);


    @Query("select count(a) " +
            "from AttendanceEntity a " +
            "where a.employee.employeeId = :studentId " +
            "and month(a.date) = :month " +
            "and year(a.date) = :year " +
            "and a.checkInStatus = 'absent'" +
            "and a.isPaidLeave = false")
    int countUnpaidLeaves(@Param("studentId") Long studentId, @Param("month") int month, @Param("year") int year);
}
