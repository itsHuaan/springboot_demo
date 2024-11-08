package org.example.springboot_demo.utils.specifications;

import jakarta.persistence.criteria.*;
import org.example.springboot_demo.entities.AttendanceEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceSpecifications {
    public static Specification<AttendanceEntity> hasEmployeeId(Long employeeId) {
        return (Root<AttendanceEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("employee").get("employeeId"), employeeId);
    }

    public static Specification<AttendanceEntity> hasMonth(int month) {
        return (Root<AttendanceEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.function("month", Integer.class, root.get("date")), month);
    }

    public static Specification<AttendanceEntity> hasYear(int year) {
        return (Root<AttendanceEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.function("year", Integer.class, root.get("date")), year);
    }

    public static Specification<AttendanceEntity> hasDate(LocalDate date) {
        return (Root<AttendanceEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.function("date", LocalDate.class, root.get("date")), date);
    }

    public static Specification<AttendanceEntity> hasCheckOut() {
        return (Root<AttendanceEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.isNotNull(root.get("checkOut"));
    }

    public static Specification<AttendanceEntity> hasLatestCheckOut() {
        return (Root<AttendanceEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
            assert criteriaQuery != null;
            Subquery<LocalTime> subquery = criteriaQuery.subquery(LocalTime.class);
            Root<AttendanceEntity> subRoot = subquery.from(AttendanceEntity.class);
            subquery.select(criteriaBuilder.greatest(subRoot.get("checkOut").as(LocalTime.class)));
            subquery.where(
                    criteriaBuilder.equal(subRoot.get("employee").get("employeeId"), root.get("employee").get("employeeId")),
                    criteriaBuilder.equal(subRoot.get("date"), root.get("date"))
            );
            return criteriaBuilder.equal(root.get("checkOut"), subquery);
        };
    }
}
