package org.example.springboot_demo.utils.specifications;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.example.springboot_demo.entities.OTRegistrationEntity;
import org.springframework.data.jpa.domain.Specification;

public class OTRegistrationSpecifications {
    public static Specification<OTRegistrationEntity> hasEmployeeId(Long employeeId) {
        return (Root<OTRegistrationEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("employee").get("employeeId"), employeeId);
    }
}
