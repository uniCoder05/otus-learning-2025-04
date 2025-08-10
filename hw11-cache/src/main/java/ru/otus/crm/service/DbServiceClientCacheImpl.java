package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.MyKey;
import ru.otus.crm.model.Client;

public class DbServiceClientCacheImpl implements DBServiceClient {

    private final DBServiceClient dbServiceClient;
    private final HwCache<MyKey, Client> cache;

    public DbServiceClientCacheImpl(DBServiceClient dbServiceClient, HwCache<MyKey, Client> cache) {
        this.dbServiceClient = dbServiceClient;
        this.cache = cache;
    }

    @Override
    public Client saveClient(Client client) {
        Client savedClient = dbServiceClient.saveClient(client);
        cache.put(createKey(savedClient.getId()), savedClient);
        return savedClient;
    }

    @Override
    public Optional<Client> getClient(long id) {
        Client cachedClient = cache.get(createKey(id));
        if (cachedClient != null) {
            return Optional.of(cachedClient);
        }
        Optional<Client> clientFromDb = dbServiceClient.getClient(id);
        clientFromDb.ifPresent(client -> cache.put(createKey(id), client));
        return clientFromDb;
    }

    @Override
    public List<Client> findAll() {
        return dbServiceClient.findAll();
    }

    private MyKey createKey(long id) {
        return new MyKey(id);
    }
}
