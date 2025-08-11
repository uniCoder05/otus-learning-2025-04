package ru.otus.demo;

import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.service.DbServiceClientImpl;

public final class MyUtilClass {

    private static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    @Getter
    private static SessionFactory sessionFactory;

    @Getter
    private static DBServiceClient dbServiceClient;

    private MyUtilClass() {}

    public static void init() {
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
}
