package ru.otus.protobuf.service;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import ru.otus.protobuf.GenerateValuesRequest;
import ru.otus.protobuf.RemoteGeneratingServiceGrpc;
import ru.otus.protobuf.ValueResponse;

@SuppressWarnings({"squid:S2142", "squid:S106"})
@Slf4j
public class RemoteGeneratingServiceImpl extends RemoteGeneratingServiceGrpc.RemoteGeneratingServiceImplBase {

    @Override
    public void generateValues(GenerateValuesRequest request, StreamObserver<ValueResponse> responseObserver) {
        int firstValue = request.getFirstValue();
        int lastValue = request.getLastValue();
        log.info("request generating new numbers from {} to {}", firstValue, lastValue);
        var currentValue = new AtomicInteger(firstValue);

        var executor = Executors.newScheduledThreadPool(1);
        Runnable taskResponse = () -> {
            var newValue = currentValue.incrementAndGet();
            var response = ValueResponse.newBuilder().setValue(newValue).build();
            responseObserver.onNext(response);
            if (newValue == lastValue) {
                executor.shutdown();
                responseObserver.onCompleted();
                log.info("generation is completed");
            }
        };
        executor.scheduleAtFixedRate(taskResponse, 0, 2, TimeUnit.SECONDS);
    }
}
