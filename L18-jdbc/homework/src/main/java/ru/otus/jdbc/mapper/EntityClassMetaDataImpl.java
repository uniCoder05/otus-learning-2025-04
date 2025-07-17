package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;
import ru.otus.annotations.Id;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Class<T> entityClass;
    private Field idField;

    public EntityClassMetaDataImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public String getName() {
        return entityClass.getSimpleName();
    }

    @Override
    public Constructor<T> getConstructor() throws NoSuchMethodException {
        return entityClass.getDeclaredConstructor();
    }

    @Override
    public Field getIdField() {
        if (idField != null) {
            return idField;
        }
        Predicate<Field> isId = (f) -> f.isAnnotationPresent(Id.class);
        return getAllFields().stream()
                .filter(isId)
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("Entity class " + getName() + " does not have an @Id field"));
    }

    @Override
    public List<Field> getAllFields() {
        return List.of(entityClass.getDeclaredFields());
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return getAllFields().stream().filter(f -> !f.equals(getIdField())).toList();
    }
}
