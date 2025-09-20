package ru.otus.protobuf;

import io.grpc.ServerBuilder;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import ru.otus.protobuf.service.RemoteGeneratingServiceImpl;

@SuppressWarnings({"squid:S106"})
@Slf4j
public class GRPCServer {

    public static final int SERVER_PORT = 8190;

    public static void main(String[] args) throws IOException, InterruptedException {

        var remoteGeneratingService = new RemoteGeneratingServiceImpl();
        var server = ServerBuilder.forPort(SERVER_PORT)
                .addService(remoteGeneratingService)
                .build();

        server.start();

        /*
            Для гарантии корректного завершения gRPC-сервера.
            При этом сервер:
             - перестанет принимать новые соединения;
             - завершит обрабатываемые запросы;
             - освободит сетевые порты и другие ресурсы.
         */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown request received");
            server.shutdown();
            log.info("Server is stopped");
        }));

        log.info("server waiting for client connections...");
        server.awaitTermination();
    }
}
