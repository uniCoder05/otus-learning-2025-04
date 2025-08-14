package ru.otus.dao;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import ru.otus.crm.model.User;

@SuppressWarnings("java:S2068")
public class InMemoryUserDao implements UserDao {

    public static final String DEFAULT_PASSWORD = "11111";
    private final SecureRandom random = new SecureRandom();
    private final Map<Long, User> users;

    public InMemoryUserDao() {
        users = new HashMap<>();
        users.put(1L, new User(1L, "Алексей Волков", "admin", DEFAULT_PASSWORD));
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.values().stream().filter(v -> v.getLogin().equals(login)).findFirst();
    }
}
