package org.example.springboot_demo.mappers;

public interface IBaseMapper<DTO, Model, Entity> {
    DTO toDTO(Entity entity);

    Entity toEntity(Model model);
}
