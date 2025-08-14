package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import ru.otus.cache.HwCache;
import ru.otus.crm.model.Client;

public class DbServiceClientCacheImpl implements DBServiceClient {

    private final DBServiceClient dbServiceClient;
    private final HwCache<Long, Client> cache;

    public DbServiceClientCacheImpl(DBServiceClient dbServiceClient, HwCache<Long, Client> cache) {
        this.dbServiceClient = dbServiceClient;
        this.cache = cache;
    }

    @Override
    public Client saveClient(Client client) {
        Client savedClient = dbServiceClient.saveClient(client);
        cache.put(savedClient.getId(), savedClient);
        return savedClient;
    }

    @Override
    public Optional<Client> getClient(long id) {
        Client cachedClient = cache.get(id);
        if (cachedClient != null) {
            return Optional.of(cachedClient);
        }
        Optional<Client> clientFromDb = dbServiceClient.getClient(id);
        clientFromDb.ifPresent(client -> cache.put(id, client));
        return clientFromDb;
    }

    @Override
    public List<Client> findAll() {
        return dbServiceClient.findAll();
    }
}
