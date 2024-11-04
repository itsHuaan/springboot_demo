package org.example.springboot_demo.repositories;

import org.example.springboot_demo.entities.AttendanceEntity;
import org.example.springboot_demo.entities.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IAttendanceRepository extends JpaRepository<AttendanceEntity, Long> {
    List<AttendanceEntity> findByDate(LocalDate Date);
    Optional<AttendanceEntity> findByStudent_StudentIdAndDate(long student_studentId, LocalDate date);
}
