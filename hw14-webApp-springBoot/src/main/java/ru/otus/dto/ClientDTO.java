package ru.otus.dto;

import java.util.List;

public record ClientDTO(Long id, String name, String address, List<String> phones) {}
