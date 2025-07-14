package ru.otus.processor;

import ru.otus.model.Message;

@SuppressWarnings("java:S1135")
public interface Processor {
    Message process(Message message);
}
