package org.example.springboot_demo.services;

import java.util.List;

public interface IBaseService<DTO, Entity, ID> {
    List<DTO> findAll();
    DTO findById(ID id);
    DTO save(Entity entity);
    int delete(ID id);
}
