package ru.otus.protobuf.observer;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import ru.otus.protobuf.ValueResponse;

@Slf4j
public class ClientStreamObserver implements StreamObserver<ValueResponse> {

    private int lastValueFromServer = 0;

    @Override
    public void onNext(ValueResponse valueResponse) {
        int lastValue = valueResponse.getValue();
        log.info("last value from server: {}", lastValue);
        setLastValueFromServer(lastValue);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("error", throwable);
    }

    @Override
    public void onCompleted() {
        log.info("request is completed");
    }

    public synchronized void setLastValueFromServer(int lastValueFromServer) {
        this.lastValueFromServer = lastValueFromServer;
    }

    public synchronized int getLastValueAndReset() {
        int prevLastValue = this.lastValueFromServer;
        this.lastValueFromServer = 0;
        return prevLastValue;
    }
}
