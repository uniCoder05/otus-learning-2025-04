package ru.petrelevich.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import ru.petrelevich.domain.Message;

public interface MessageRepository extends ReactiveCrudRepository<Message, Long> {

    @Query("select * from message where room_id = :room_id order by id")
    Flux<Message> findByRoomId(@Param("roomId") String roomId);

    @Override
    @Query("select * from message")
    @NonNull
    Flux<Message> findAll();
}
