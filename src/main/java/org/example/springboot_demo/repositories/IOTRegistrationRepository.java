package org.example.springboot_demo.repositories;

import org.example.springboot_demo.entities.OTRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOTRegistrationRepository extends JpaRepository<OTRegistrationEntity, Long> {
}
