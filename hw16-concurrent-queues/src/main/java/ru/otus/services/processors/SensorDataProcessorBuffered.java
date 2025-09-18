package ru.otus.services.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;
import ru.otus.lib.SensorDataBufferedWriter;

// Этот класс нужно реализовать
@SuppressWarnings({"java:S1068", "java:S125"})
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final PriorityBlockingQueue<SensorData> dataBuffer;
    private final Object flushLock = new Object();

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
        this.dataBuffer = new PriorityBlockingQueue<>(bufferSize);
    }

    @Override
    public void process(SensorData data) {
        synchronized (flushLock) {
            if (dataBuffer.size() >= bufferSize) {
                flush();
            }
            dataBuffer.offer(data);
        }
    }

    public void flush() {
        List<SensorData> bufferedData = new ArrayList<>();
        dataBuffer.drainTo(bufferedData);

        if (bufferedData.isEmpty()) {
            return;
        }
        try {
            writer.writeBufferedData(bufferedData);
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
            dataBuffer.addAll(bufferedData);
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
