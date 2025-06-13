package ru.otus;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;


class MyIoc {

    private MyIoc() {}

    public static <T> T createProxy(T t) {
        InvocationHandler handler = new MyDemoInvocationHandler(t);
        return (T) Proxy.newProxyInstance(
                MyIoc.class.getClassLoader(), t.getClass().getInterfaces(), handler);
    }
}
