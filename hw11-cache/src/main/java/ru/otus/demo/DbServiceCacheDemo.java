package ru.otus.demo;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.*;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.service.DbServiceClientCacheImpl;

public class DbServiceCacheDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceCacheDemo.class);

    private static final Supplier<Long> TIMER = System::nanoTime;
    private static final String M = "ns";

    public static void main(String[] args) throws InterruptedException {
        MyUtilClass.init();
        SessionFactory sessionFactory = MyUtilClass.getSessionFactory();
        DBServiceClient dbServiceClient = MyUtilClass.getDbServiceClient();
        // --- Инициализация  dbServiceClientCache ---
        HwCache<Long, Client> cache = new MyCache<>();
        HwListener<Long, Client> cacheListener = new CacheListener<>();
        cache.addListener(cacheListener);
        var dbServiceClientCache = new DbServiceClientCacheImpl(dbServiceClient, cache);

        // --- Тестирование ---
        log.info("\n--- Тестирование операции INSERT и PUT в кэш ---");
        batchInsertClient(dbServiceClientCache, 10);

        log.info("\n--- Тестирование операции GET ---");
        var clients = dbServiceClientCache.findAll();
        cache.clear(); // очищаем кэш, чтобы первый get запрос всегда шёл к БД

        for (Client client : clients) {
            getClientTimeTest(client, dbServiceClientCache, false);
            getClientTimeTest(client, dbServiceClientCache, true);
            getClientTimeTest(client, dbServiceClient, false);
        }

        log.info("\n--- Тестирование операции UPDATE ---");
        // Клиент должен обновиться и в БД и кэше
        long id = clients.getFirst().getId();
        Client updatedClient = new Client(id, "UpdatedClient");
        dbServiceClientCache.saveClient(updatedClient);
        getClientTimeTest(updatedClient, dbServiceClient, false);
        getClientTimeTest(updatedClient, dbServiceClientCache, true);

        log.info("\n--- Демонстрация очистки кэша с помощью WeakHashMap и GC ---");
        log.info("Current MyCache size: {}", cache.size());
        log.info("Принудительный вызов GC...");
        System.gc();
        TimeUnit.SECONDS.sleep(1);
        log.info("MyCache size после GC : {}", cache.size());

        cache.removeListener(cacheListener);
        sessionFactory.close();
    }

    private static void batchInsertClient(DBServiceClient dbServiceClient, long limit) {
        long startId = 1
                + dbServiceClient.findAll().stream()
                        .mapToLong(Client::getId)
                        .max()
                        .orElse(0L);
        long endId = startId + limit;
        for (long i = startId; i < endId; i++) {
            String name = "Client_" + i;
            dbServiceClient.saveClient(new Client(name));
        }
    }

    private static void getClientTimeTest(Client client, DBServiceClient dbServiceClient, boolean haveCache) {
        String cache = haveCache ? "(CACHE)" : "";
        long start = TIMER.get();
        Client returnedClient = dbServiceClient.getClient(client.getId()).orElse(null);
        long end = TIMER.get();
        log.info("Полученный client: {}, затраченное время: {} {}", returnedClient, duration(start, end), cache);
    }

    private static String duration(long start, long end) {
        return (end - start) + " " + M;
    }
}
