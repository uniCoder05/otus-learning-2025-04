package ru.otus.processor.homework;

import ru.otus.model.Message;
import ru.otus.processor.Processor;

public class ProcessorExceptionEvenSecond implements Processor {
    private final DateTimeProvider dateTimeProvider;

    public ProcessorExceptionEvenSecond() {
        this(new TimeProvider());
    }

    public ProcessorExceptionEvenSecond(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public Message process(Message message) {
        var sec = dateTimeProvider.now().getSecond();
        if (sec % 2 == 0) {
            throw new EvenSecondException(String.valueOf(sec));
        }
        return message;
    }
}
