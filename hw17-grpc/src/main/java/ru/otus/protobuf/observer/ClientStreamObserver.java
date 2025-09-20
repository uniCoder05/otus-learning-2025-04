package ru.otus.protobuf.observer;

import io.grpc.stub.StreamObserver;
import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import ru.otus.protobuf.ValueResponse;

@Slf4j
@Setter(onMethod_ = {@Synchronized})
public class ClientStreamObserver implements StreamObserver<ValueResponse> {

    int lastValueFromServer = 0;

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

    @Synchronized
    public int getLastValueAndReset() {
        int prevLastValue = this.lastValueFromServer;
        this.lastValueFromServer = 0;
        return prevLastValue;
    }
}
