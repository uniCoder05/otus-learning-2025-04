package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import ru.otus.annotations.Id;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Class<T> entityClass;
    private final String className;
    private final List<Field> allFields;
    private final Field idField;
    private final List<Field> fieldsWithoutId;
    private final Constructor<T> constructor;

    public EntityClassMetaDataImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
        className = entityClass.getSimpleName();
        allFields = List.of(entityClass.getDeclaredFields());
        idField = idField();
        fieldsWithoutId = allFields.stream().filter(f -> !f.equals(idField())).toList();
        try {
            constructor = entityClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Class " + entityClass.getSimpleName() + " must have a no-argument constructor", e);
        }
    }

    @Override
    public String getName() {
        return className;
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWithoutId;
    }

    private Field idField() {

        return allFields.stream()
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Entity class " + entityClass.getSimpleName() + " does not have an @Id field"));
    }
}
