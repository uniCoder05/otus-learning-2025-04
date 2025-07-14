package ru.otus;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.handler.ComplexProcessor;
import ru.otus.listener.homework.HistoryListener;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;
import ru.otus.processor.LoggerProcessor;
import ru.otus.processor.homework.ProcessorExceptionEvenSecond;
import ru.otus.processor.homework.ProcessorSwapField11Field12;

public class HomeWork {
    private static final Logger log = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
        var processors =
                List.of(new LoggerProcessor(new ProcessorSwapField11Field12()), new ProcessorExceptionEvenSecond());
        var complexProcessor = new ComplexProcessor(processors, ex -> {});
        var listenerPrinter = new HistoryListener();
        complexProcessor.addListener(listenerPrinter);

        var filed13 = new ObjectForMessage();

        filed13.setData(List.of("data1", "data2"));

        var message = new Message.Builder(1L)
                .field11("field11")
                .field12("field12")
                .field13(filed13)
                .build();

        var result = complexProcessor.handle(message);
        log.info("result:{}", result);

        complexProcessor.removeListener(listenerPrinter);
    }
}
