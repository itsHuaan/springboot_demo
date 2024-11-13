package org.example.springboot_demo.utils.specifications;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.example.springboot_demo.entities.OtpEntity;
import org.springframework.data.jpa.domain.Specification;

public class OtpSpecifications {
    public static Specification<OtpEntity> isActive() {
        return (Root<OtpEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), 1);
    }

    public static Specification<OtpEntity> equalTo(String confirmationCode) {
        return (Root<OtpEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("confirmationCode"), confirmationCode);
    }
}
