package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

/** Сохраняет объект в базу, читает объект из базы */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {
    private static final Logger log = LoggerFactory.getLogger(DataTemplateJdbc.class);

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(
            DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        String selectByIdSql = entitySQLMetaData.getSelectByIdSql();
        log.info("selectByIdSql: {} id: {}", selectByIdSql, id);

        return dbExecutor.executeSelect(connection, selectByIdSql, List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return createInstanceFromResultSet(rs);
                }
                return null;
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        String selectAllSql = entitySQLMetaData.getSelectAllSql();
        log.info("selectAllSql: {}", selectAllSql);

        return dbExecutor
                .executeSelect(connection, selectAllSql, Collections.emptyList(), rs -> {
                    List<T> listEntity = new ArrayList<>();
                    try {
                        while (rs.next()) {
                            listEntity.add(createInstanceFromResultSet(rs));
                        }

                        return listEntity;
                    } catch (SQLException e) {
                        throw new DataTemplateException(e);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T entity) {
        String insertSql = entitySQLMetaData.getInsertSql();
        log.info("insertSql: {} entity: {}", insertSql, entity);
        try {
            final List<Object> params = new ArrayList<>();
            for (Field field : entityClassMetaData.getFieldsWithoutId()) {
                field.setAccessible(true);
                params.add(field.get(entity));
            }
            return dbExecutor.executeStatement(connection, insertSql, params);
        } catch (IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T entity) {
        String updateSql = entitySQLMetaData.getUpdateSql();
        log.info("updateSql: {} entity: {}", updateSql, entity);
        try {
            final List<Object> params = new ArrayList<>();
            for (Field field : entityClassMetaData.getFieldsWithoutId()) {
                field.setAccessible(true);
                params.add(field.get(entity));
            }
            Field idField = entityClassMetaData.getIdField();
            idField.setAccessible(true);
            params.add(idField.get(entity)); // Добавляем ID в конец для WHERE условия

            dbExecutor.executeStatement(connection, updateSql, params);
        } catch (IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
    }

    private T createInstanceFromResultSet(ResultSet rs) throws SQLException {
        try {
            Constructor<T> constructor = entityClassMetaData.getConstructor();
            T instance = constructor.newInstance();
            for (Field field : entityClassMetaData.getAllFields()) {
                field.setAccessible(true);
                Object value = rs.getObject(field.getName());
                field.set(instance, value);
            }

            return instance;
        } catch (NoSuchMethodException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
