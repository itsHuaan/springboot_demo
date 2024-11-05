package org.example.springboot_demo.mappers;

public interface IBaseMapper<T, U, V> {
    T toDTO(V entity);

    V toEntity(U model);
}
