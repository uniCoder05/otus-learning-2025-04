package ru.otus.appcontainer;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.appcontainer.exceptions.AppComponentCreationException;
import ru.otus.appcontainer.exceptions.AppComponentNotFoundException;
import ru.otus.appcontainer.exceptions.TooManyAppComponentsException;

@SuppressWarnings({"this-escape", "squid:S1068"})
public class AppComponentsContainerImpl implements AppComponentsContainer {
    private static final Logger log = LoggerFactory.getLogger(AppComponentsContainerImpl.class);

    private final Map<String, Object> appComponentsByName = new HashMap<>();
    private final Map<Class<?>, List<Object>> appComponentsByType = new HashMap<>();
    private static final Class<AppComponentsContainerConfig> CONFIG_ANNOTATION_CLASS =
            AppComponentsContainerConfig.class;

    public AppComponentsContainerImpl(Class<?>... initialConfigClasses) {
        batchProcessConfig(initialConfigClasses);
    }

    public AppComponentsContainerImpl(String packageNameConfig) {
        var reflections = new Reflections(packageNameConfig);
        var annotatedTypes = TypesAnnotated.with(CONFIG_ANNOTATION_CLASS);
        Class<?>[] classes =
                reflections.get(SubTypes.of(annotatedTypes).asClass()).toArray(Class<?>[]::new);
        batchProcessConfig(classes);
    }

    private void batchProcessConfig(Class<?>... configClasses) {
        Arrays.stream(configClasses)
                .sorted(Comparator.comparingInt(
                        clazz -> clazz.getAnnotation(CONFIG_ANNOTATION_CLASS).order()))
                .forEach(this::processConfig);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        Class<AppComponent> appComponentClass = AppComponent.class;

        Object configInstance;
        try {
            configInstance = configClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new AppComponentCreationException(
                    String.format("Ошибка создания экземпляра конфигурационного класса %s", configClass.getName()), e);
        }

        Predicate<Method> isComponentMethod = (m) -> m.isAnnotationPresent(appComponentClass);
        Comparator<Method> byOrderComparator =
                Comparator.comparingInt(m -> m.getAnnotation(appComponentClass).order());

        List<Method> componentMethods = Arrays.stream(configClass.getDeclaredMethods())
                .filter(isComponentMethod)
                .sorted(byOrderComparator)
                .toList();

        for (Method method : componentMethods) {
            String componentName = method.getAnnotation(appComponentClass).name();
            log.info("Метод: {} Имя компонента: {}", method.getName(), componentName);
            if (appComponentsByName.containsKey(componentName)) {
                throw new AppComponentCreationException(String.format("Дубликат имени компонента '%s'", componentName));
            }

            Object[] args = Arrays.stream(method.getParameterTypes())
                    .map(this::getAppComponent)
                    .toArray();

            try {
                Object component = method.invoke(configInstance, args);
                appComponentsByName.put(componentName, component);
                putComponentByType(component.getClass(), component);
                putComponentByType(method.getReturnType(), component);

            } catch (Exception e) {
                throw new AppComponentCreationException(
                        String.format(
                                "Ошибка создания компонента: '%s' Вызываемый метод: '%s'.",
                                componentName, method.getName()),
                        e);
            }
        }
    }

    private void putComponentByType(Class<?> componentType, Object component) {
        appComponentsByType
                .computeIfAbsent(componentType, val -> new ArrayList<>())
                .add(component);
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(
                    String.format("Указанный класс не является конфигурационным %s", configClass.getName()));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Object> foundComponents = appComponentsByType.get(componentClass);
        if (foundComponents == null || foundComponents.isEmpty()) {
            throw new AppComponentNotFoundException(
                    String.format("Компонент с типом '%s' не найден", componentClass.getName()));
        }
        if (foundComponents.size() > 1) {
            throw new TooManyAppComponentsException(
                    String.format("Найдено более одного компонента с типом '%s'", componentClass.getName()));
        }

        return (C) foundComponents.getFirst();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C getAppComponent(String componentName) {

        return (C) Optional.ofNullable(appComponentsByName.get(componentName))
                .orElseThrow(() -> new AppComponentNotFoundException("Компонент не найден: " + componentName));
    }
}
