package org.example.springboot_demo.repositories;

import org.example.springboot_demo.entities.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.Optional;

public interface IAttendanceRepository extends JpaRepository<AttendanceEntity, Long>, JpaSpecificationExecutor<AttendanceEntity> {
    Optional<AttendanceEntity> findFirstByEmployee_EmployeeIdAndDate(long employee_employeeId, LocalDate date);
}
