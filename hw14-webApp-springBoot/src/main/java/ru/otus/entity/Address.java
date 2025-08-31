package ru.otus.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("addresses")
public record Address(@Id Long id, String street) {}
