package ru.otus;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MyIoc {
    private static final Logger logger = LoggerFactory.getLogger(MyIoc.class);

    private MyIoc() {}

    public static <T> T createMyClass(Class<? extends T> implClass) {
        T t = create(implClass);

        return createProxy(t);
    }

    private static <T> T createProxy(T t) {
        InvocationHandler handler = new MyDemoInvocationHandler(t);
        return (T) Proxy.newProxyInstance(
                MyIoc.class.getClassLoader(), t.getClass().getInterfaces(), handler);
    }

    private static <T> T create(Class<? extends T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            logger.error("не удалось создать объект класса: {}", clazz.getName());
        }
        return null;
    }
}
