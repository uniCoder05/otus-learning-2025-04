package ru.otus.crm.dto;

public interface Mapper<D, E> {

    D toDTO(E e);

    E toEntity(D d);
}
