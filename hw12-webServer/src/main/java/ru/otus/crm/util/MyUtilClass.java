package ru.otus.crm.util;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.otus.cache.CacheListener;
import ru.otus.cache.HwCache;
import ru.otus.cache.HwListener;
import ru.otus.cache.MyCache;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.service.DbServiceClientCacheImpl;
import ru.otus.crm.service.DbServiceClientImpl;

public final class MyUtilClass {

    private static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    @Getter
    private static SessionFactory sessionFactory;

    @Getter
    private static DBServiceClient dbServiceClient;

    private MyUtilClass() {}

    public static DBServiceClient initializeDBServiceClient() {
        MyUtilClass.init();
        HwCache<Long, Client> cache = new MyCache<>();
        HwListener<Long, Client> cacheListener = new CacheListener<>();
        cache.addListener(cacheListener);
        var dbServiceClientCache = new DbServiceClientCacheImpl(dbServiceClient, cache);
        if (dbServiceClientCache.findAll().isEmpty()) {
            insertClients(dbServiceClientCache);
        }

        return dbServiceClientCache;
    }

    private static void init() {
        sessionFactory = createSessionFactory();
        dbServiceClient = createDBServiceClient();
    }

    private static SessionFactory createSessionFactory() {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        return HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);
    }

    private static DBServiceClient createDBServiceClient() {
        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);

        return new DbServiceClientImpl(transactionManager, clientTemplate);
    }

    private static void insertClients(DBServiceClient dbServiceClient) {
        List<Client> clients = new ArrayList<>();
        clients.add(new Client("Ivan Ivanov", new Address("Lenina"), List.of(new Phone("81111111111"))));
        clients.add(new Client("Petr Petrov", new Address("Metallurgov"), List.of(new Phone("82222222222"))));
        clients.add(new Client("Sidr Sidorov", new Address("Kirova"), List.of(new Phone("83333333333"))));
        clients.add(new Client("Alexey Vorobiev", new Address("Pobedi"), List.of(new Phone("84444444444"))));
        clients.add(new Client("Andrey Volkov", new Address("Gagarina"), List.of(new Phone("85555555555"))));
        clients.add(new Client("Maksim Kozlov", new Address("Osipenko"), List.of(new Phone("86666666666"))));
        clients.add(new Client("Nikita Maslov", new Address("Stroiteley"), List.of(new Phone("87777777777"))));
        clients.add(new Client("Dmitry Stadnik", new Address("Elizarova"), List.of(new Phone("88888888888"))));
        clients.add(new Client(
                null,
                "Alexander Dolz",
                new Address("Sovetskaya"),
                List.of(new Phone("89999999999"), new Phone("80000000000"))));
        for (Client client : clients) {
            dbServiceClient.saveClient(client);
        }
    }
}
