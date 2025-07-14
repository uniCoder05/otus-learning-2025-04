package ru.otus.processor.homework;

import java.time.LocalDateTime;

public class TimeProvider implements DateTimeProvider {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
