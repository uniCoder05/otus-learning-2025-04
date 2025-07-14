package ru.otus.processor.homework;

import ru.otus.model.Message;
import ru.otus.processor.Processor;

public class ProcessorSwapField11Field12 implements Processor {

    @Override
    public Message process(Message message) {
        String tmp = message.getField11();
        return message.toBuilder().field11(message.getField12()).field12(tmp).build();
    }
}
