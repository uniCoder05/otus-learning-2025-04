package ru.otus.api.model;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class SensorData implements Comparable<SensorData> {
    private final LocalDateTime measurementTime;
    private final String room;
    private final Double value;

    public SensorData(LocalDateTime measurementTime, String room, Double value) {
        this.measurementTime = measurementTime;
        this.room = room;
        this.value = value;
    }

    @Override
    public String toString() {
        return "SensorData{" + "measurementTime="
                + measurementTime + ", room='"
                + room + '\'' + ", value="
                + value + '}';
    }
    //Для требуемой сортировки в PriorityBlockingQueue
    @Override
    public int compareTo(SensorData other) {
        return this.measurementTime.compareTo(other.getMeasurementTime());
    }
}
