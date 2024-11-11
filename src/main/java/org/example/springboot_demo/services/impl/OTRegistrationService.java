package org.example.springboot_demo.services.impl;

import org.example.springboot_demo.dtos.OTRegistrationDto;
import org.example.springboot_demo.entities.OTRegistrationEntity;
import org.example.springboot_demo.mappers.impl.OTRegistrationMapper;
import org.example.springboot_demo.repositories.IOTRegistrationRepository;
import org.example.springboot_demo.services.IOTRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OTRegistrationService implements IOTRegistrationService {

    private final IOTRegistrationRepository otRegistrationRepository;
    private final OTRegistrationMapper otRegistrationMapper;

    @Autowired
    public OTRegistrationService(IOTRegistrationRepository otRegistrationRepository,
                                 OTRegistrationMapper otRegistrationMapper) {
        this.otRegistrationRepository = otRegistrationRepository;
        this.otRegistrationMapper = otRegistrationMapper;
    }

    @Override
    public List<OTRegistrationDto> findAll() {
        return otRegistrationRepository.findAll().stream().map(otRegistrationMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public OTRegistrationDto findById(Long id) {
        return otRegistrationMapper.toDTO(Objects.requireNonNull(otRegistrationRepository.findById(id).orElse(null)));
    }

    @Override
    public OTRegistrationDto save(OTRegistrationEntity entity) {
        return otRegistrationMapper.toDTO(otRegistrationRepository.save(entity));
    }

    @Override
    public int delete(Long id) {
        return 0;
    }
}
