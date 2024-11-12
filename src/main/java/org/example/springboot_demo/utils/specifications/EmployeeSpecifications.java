package org.example.springboot_demo.utils.specifications;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.example.springboot_demo.entities.EmployeeEntity;
import org.example.springboot_demo.entities.OTRegistrationEntity;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecifications {
    public static Specification<EmployeeEntity> hasEmail() {
        return (Root<EmployeeEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.isNotNull(root.get("email"));
    }
}
