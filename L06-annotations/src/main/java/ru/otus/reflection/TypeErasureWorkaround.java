package ru.otus.reflection;

import java.lang.reflect.Field;
import java.util.Arrays;

@SuppressWarnings({"java:S1068", "java:S125"})
class NodeWorkaround<T extends Comparable<T>> {
    private final Class<T> nodeType;
    private final T data;
    private final NodeWorkaround<T> next;

    public NodeWorkaround(Class<T> nodeType, T data, NodeWorkaround<T> next) {
        this.nodeType = nodeType;
        this.data = data;
        this.next = next;
    }

    public T getData() {
        return data;
    }

    public Class<T> getNodeType() {
        return nodeType;
    }
}

@SuppressWarnings({"java:S106", "java:S2133"})
public class TypeErasureWorkaround {
    public static void main(String[] args) throws NoSuchFieldException {
        // Обходное решение - передавать и хранить тип, если он известен при компиляции
        var node = new NodeWorkaround<String>(String.class, "first node", null);

        var clazz = node.getClass();
        var typeParameters = clazz.getTypeParameters();
        System.out.println("Class generic parameters: " + Arrays.toString(typeParameters));
        System.out.println("First parameter bound: " + typeParameters[0].getBounds()[0].getTypeName());
        System.out.println("Node type: " + node.getNodeType());

        Field field = clazz.getDeclaredField("data");
        System.out.println("'data' field type: " + field.getType().getCanonicalName());
    }
}
