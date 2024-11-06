package org.example.springboot_demo.repositories;

import org.example.springboot_demo.entities.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IAttendanceRepository extends JpaRepository<AttendanceEntity, Long> {
    @Query("select a from AttendanceEntity a " +
            "where a.checkOut = (" +
            "select max (a2.checkOut) from AttendanceEntity a2 " +
            "where a2.employee.employeeId = a.employee.employeeId " +
            "and a2.date = a.date) " +
            "and a.date = :date")
    List<AttendanceEntity> findByDate(LocalDate date);

    Optional<AttendanceEntity> findFirstByEmployee_EmployeeIdAndDate(long employee_employeeId, LocalDate date);
    @Query("select a from AttendanceEntity a " +
            "where a.checkOut = (" +
            "select max (a2.checkOut) from AttendanceEntity a2 " +
            "where a2.employee.employeeId = a.employee.employeeId " +
            "and a2.date = a.date)")
    List<AttendanceEntity> findLastRecordByCheckOut();

    @Query("select count(distinct a.date) " +
            "from AttendanceEntity a " +
            "where a.employee.employeeId = :employeeId " +
            "and month(a.date) = :month " +
            "and year(a.date) = :year " +
            "and a.checkInStatus = 'absent'" +
            "and a.isPaidLeave = true")
    int countPaidLeaves(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);

    @Query("select count(distinct a.date) " +
            "from AttendanceEntity a " +
            "where a.employee.employeeId = :employeeId " +
            "and month(a.date) = :month " +
            "and year(a.date) = :year " +
            "and a.checkInStatus = 'absent'" +
            "and a.isPaidLeave = false")
    int countUnpaidLeaves(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);

    @Query("select count(distinct a.date) " +
            "from AttendanceEntity a " +
            "where a.employee.employeeId = :employeeId " +
            "and month(a.date) = :month " +
            "and year(a.date) = :year " +
            "and a.checkInStatus = 'lateArrival'")
    int countLateArrivals(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);

    @Query("select count(distinct a.date) " +
            "from AttendanceEntity a " +
            "where a.employee.employeeId = :employeeId " +
            "and month(a.date) = :month " +
            "and year(a.date) = :year " +
            "and a.checkOutStatus = 'leaveEarly'")
    int countLeaveEarly(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);

    @Query("select coalesce(sum(CURRENT_TIMESTAMP - a.checkIn), 0) " +
            "from AttendanceEntity a " +
            "where a.employee.employeeId = :employeeId " +
            "and month(a.date) = :month " +
            "and year(a.date) = :year " +
            "and a.checkInStatus = 'lateArrival'")
    long sumLateArrivalTime(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);

    @Query("select coalesce(sum(CURRENT_TIMESTAMP - a.checkOut), 0) " +
            "from AttendanceEntity a " +
            "where a.employee.employeeId = :employeeId " +
            "and month(a.date) = :month " +
            "and year(a.date) = :year " +
            "and a.checkOutStatus = 'leaveEarly'")
    long sumEarlyLeaveTime(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);
}
