package ru.otus.protobuf;

import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.otus.protobuf.observer.ClientStreamObserver;

@SuppressWarnings({"squid:S106", "squid:S2142"})
@Slf4j
public class GRPCClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8190;
    private static final int FIRST_VALUE = 0;
    private static final int LAST_VALUE = 30;
    private static final int CYCLE_LIMIT = 50;
    private static final int SLEEP_TIME_MILLIS = 1000;

    private int value = 0;

    public static void main(String[] args) {
        // Канал для взаимодействия с сервером и выполнения RPC
        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();
        // Stub - специальный объект для удалённого вызова процедур, sync / async - newBlockingStub / newStub
        var asyncStub = RemoteGeneratingServiceGrpc.newStub(channel);
        new GRPCClient().clientProcess(asyncStub);
    }

    private void clientProcess(RemoteGeneratingServiceGrpc.RemoteGeneratingServiceStub asyncStub) {
        GenerateValuesRequest generatingRequest = makeGenerateValuesRequest();
        ClientStreamObserver clientStreamObserver = new ClientStreamObserver();
        asyncStub.generateValues(generatingRequest, clientStreamObserver);

        for (int i = 0; i < CYCLE_LIMIT; i++) {
            var nextValue = getNextValue(clientStreamObserver);
            log.info("currentValue: {}", nextValue);
            sleep();
        }
    }

    private GenerateValuesRequest makeGenerateValuesRequest() {
        log.info("Make generate values request from {} to {}", FIRST_VALUE, LAST_VALUE);
        return GenerateValuesRequest.newBuilder()
                .setFirstValue(FIRST_VALUE)
                .setLastValue(LAST_VALUE)
                .build();
    }

    private int getNextValue(ClientStreamObserver clientStreamObserver) {
        value += clientStreamObserver.getLastValueAndReset() + 1;
        return value;
    }

    private void sleep() {
        try {
            Thread.sleep(SLEEP_TIME_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
