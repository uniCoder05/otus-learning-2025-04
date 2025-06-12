package ru.otus;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyDemoInvocationHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(MyDemoInvocationHandler.class);

    private final Object myClassInterface;
    private final Class<?> implClass;
    private final Set<Method> logMethods;

    public <T> MyDemoInvocationHandler(T myClassInterface) {
        this.myClassInterface = myClassInterface;
        this.implClass = myClassInterface.getClass();
        this.logMethods = getLogMethods();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isLogMethod(method)) {
            logger.info("executed method: {}, param: {}", method.getName(), argsToString(args));
        }
        return method.invoke(myClassInterface, args);
    }

    private boolean isLogMethod(Method m) {
        return logMethods.contains(m);
    }

    private Set<Method> getLogMethods() {
        Predicate<Method> haveLogAnnotation = (m) -> m.isAnnotationPresent(Log.class);
        Set<String> logMethodsOfImplClass = Arrays.stream(implClass.getDeclaredMethods())
                .filter(haveLogAnnotation)
                .map(this::getSgnOfMethod)
                .collect(Collectors.toCollection(HashSet::new));

        Predicate<Method> isLogMethod = (m) -> logMethodsOfImplClass.contains(getSgnOfMethod(m));

        return Arrays.stream(implClass.getInterfaces())
                .map(Class::getDeclaredMethods)
                .flatMap(Arrays::stream)
                .filter(isLogMethod)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private String getSgnOfMethod(Method m) {

        return String.format("%s(%s)", m.getName(), argsToString(m.getParameterTypes()));
    }

    private String argsToString(Object[] args) {
        String result = "";
        if (args != null) {
            result = Arrays.stream(args).map(Object::toString).collect(Collectors.joining(", "));
        }

        return result;
    }
}
