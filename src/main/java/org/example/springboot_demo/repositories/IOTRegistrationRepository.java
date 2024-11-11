package org.example.springboot_demo.repositories;

import org.example.springboot_demo.entities.OTRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IOTRegistrationRepository extends JpaRepository<OTRegistrationEntity, Long>, JpaSpecificationExecutor<OTRegistrationEntity> {
}
