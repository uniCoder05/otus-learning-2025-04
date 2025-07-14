package ru.otus.listener.homework;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import ru.otus.listener.Listener;
import ru.otus.model.Message;

public class HistoryListener implements Listener, HistoryReader {

    private final Map<Long, Message> messageStorage = new HashMap<>();

    @Override
    public void onUpdated(Message msg) {
        Message msgCopy = msg.deepCopy();
        messageStorage.put(msg.getId(), msgCopy);
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.ofNullable(messageStorage.get(id));
    }
}
