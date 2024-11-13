package org.example.springboot_demo.repositories;

import org.example.springboot_demo.entities.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IOtpRepository extends JpaRepository<OtpEntity, Long>, JpaSpecificationExecutor<OtpEntity> {
}
