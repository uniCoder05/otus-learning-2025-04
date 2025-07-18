package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {
    private final String tableName;
    private final String idFieldName;
    private final String selectAllSql;
    private final String selectByIdSql;
    private final String insertSql;
    private final String updateSql;

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> entityClassMetaData) {
        tableName = entityClassMetaData.getName();
        idFieldName = entityClassMetaData.getIdField().getName();
        selectAllSql = createSelectAllSql(tableName);
        selectByIdSql = createSelectByIdSql(tableName, idFieldName);
        insertSql = createInsertSql(entityClassMetaData);
        updateSql = createUpdateSql(entityClassMetaData);
    }

    @Override
    public String getSelectAllSql() {
        return selectAllSql;
    }

    @Override
    public String getSelectByIdSql() {
        return selectByIdSql;
    }

    @Override
    public String getInsertSql() {
        return insertSql;
    }

    @Override
    public String getUpdateSql() {
        return updateSql;
    }

    private String createSelectAllSql(String tableName) {
        return "select * from " + tableName;
    }

    private String createSelectByIdSql(String tableName, String idFieldName) {
        return "select * from " + tableName + " where " + idFieldName + " = ?";
    }

    private String createInsertSql(EntityClassMetaData<?> entityClassMetaData) {
        List<Field> fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();
        String fieldNames = fieldsWithoutId.stream().map(Field::getName).collect(Collectors.joining(", "));
        String questionSigns = fieldsWithoutId.stream().map(f -> "?").collect(Collectors.joining(", "));

        return "insert into " + tableName + " (" + fieldNames + ") values (" + questionSigns + ")";
    }

    private String createUpdateSql(EntityClassMetaData<?> entityClassMetaData) {
        String setStatements = entityClassMetaData.getFieldsWithoutId().stream()
                .map(field -> field.getName() + " = ?")
                .collect(Collectors.joining(", "));

        return "update " + tableName + " set " + setStatements + " where " + idFieldName + " = ?";
    }
}
