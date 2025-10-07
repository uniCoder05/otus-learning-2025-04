package ru.petrelevich.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.util.HtmlUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.petrelevich.domain.Message;
import ru.petrelevich.domain.Room;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private static final String TOPIC_TEMPLATE = "/topic/response.";

    private final WebClient datastoreClient;
    private final SimpMessagingTemplate template;

    public MessageController(WebClient datastoreClient, SimpMessagingTemplate template) {
        this.datastoreClient = datastoreClient;
        this.template = template;
    }

    @MessageMapping("/message.{roomId}")
    public void getMessage(@DestinationVariable("roomId") String roomId, Message message) {
        if (Room.ROOM_1408.equals(roomId)) {
            logger.error("You can't save messages in room {}", roomId);
            return;
        }
        logger.info("get message:{}, roomId:{}", message, roomId);
        saveMessage(roomId, message).subscribe(msgId -> logger.info("message send id:{}", msgId));

        convertAndSend(roomId, message);
        convertAndSend(Room.ROOM_1408, message);
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        var genericMessage = (GenericMessage<byte[]>) event.getMessage();
        var simpDestination = (String) genericMessage.getHeaders().get("simpDestination");
        if (simpDestination == null) {
            logger.error("Can not get simpDestination header, headers:{}", genericMessage.getHeaders());
            throw new ChatException("Can not get simpDestination header");
        }
        if (!simpDestination.startsWith(TOPIC_TEMPLATE)) {
            return;
        }
        var roomId = parseRoomId(simpDestination);

        var principal = event.getUser();
        if (principal == null) {
            return;
        }
        logger.info("subscription for:{}, roomId:{}, user:{}", simpDestination, roomId, principal.getName());
        // /user/f6532733-51db-4d0e-bd00-1267dddc7b21/topic/response.1
        var messages = Long.parseLong(Room.ROOM_1408) == roomId ? getAllMessages() : getMessagesByRoomId(roomId);

        messages.doOnError(ex -> logger.error("getting messages for roomId:{} failed", roomId, ex))
                .subscribe(message -> template.convertAndSendToUser(principal.getName(), simpDestination, message));
    }

    private long parseRoomId(String simpDestination) {
        try {
            var idxRoom = simpDestination.lastIndexOf(TOPIC_TEMPLATE);
            return Long.parseLong(simpDestination.substring(idxRoom).replace(TOPIC_TEMPLATE, ""));
        } catch (Exception ex) {
            logger.error("Can not get roomId", ex);
            throw new ChatException("Can not get roomId");
        }
    }

    private Mono<Long> saveMessage(String roomId, Message message) {
        return datastoreClient
                .post()
                .uri(String.format("/msg/%s", roomId))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(message)
                .exchangeToMono(response -> response.bodyToMono(Long.class));
    }

    private Flux<Message> getMessagesByRoomId(long roomId) {
        return datastoreClient
                .get()
                .uri(String.format("/msg/%s", roomId))
                .accept(MediaType.APPLICATION_NDJSON)
                .exchangeToFlux(this::responseMsg);
    }

    private Flux<Message> getAllMessages() {
        return datastoreClient
                .get()
                .uri("/msg")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchangeToFlux(this::responseMsg);
    }

    private Flux<Message> responseMsg(ClientResponse response) {
        if (response.statusCode().equals(HttpStatus.OK)) {
            return response.bodyToFlux(Message.class);
        } else {
            return response.createException().flatMapMany(Mono::error);
        }
    }

    private void convertAndSend(String roomId, Message message) {
        template.convertAndSend(
                String.format("%s%s", TOPIC_TEMPLATE, roomId), new Message(HtmlUtils.htmlEscape(message.messageStr())));
    }
}
