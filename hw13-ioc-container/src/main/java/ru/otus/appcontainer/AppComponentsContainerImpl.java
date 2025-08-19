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
    private static final Class<AppComponentsContainerConfig> CONFIG_ANNOTATION_CLASS =
            AppComponentsContainerConfig.class;
    private static final Class<AppComponent> COMPONENT_ANNOTATION_CLASS = AppComponent.class;

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

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

        Object configInstance = getConfigInstance(configClass);
        List<Method> componentMethods = getComponentMethodsSortedByOrder(configClass);
        componentMethods.forEach(m -> processComponentMethod(m, configInstance));
    }

    private Object getConfigInstance(Class<?> configClass) {
        try {
            return configClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new AppComponentCreationException(
                    String.format("Ошибка создания экземпляра конфигурационного класса %s", configClass.getName()), e);
        }
    }

    private List<Method> getComponentMethodsSortedByOrder(Class<?> configClass) {
        Predicate<Method> isComponentMethod = (m) -> m.isAnnotationPresent(COMPONENT_ANNOTATION_CLASS);
        Comparator<Method> byOrderComparator = Comparator.comparingInt(
                m -> m.getAnnotation(COMPONENT_ANNOTATION_CLASS).order());

        return Arrays.stream(configClass.getDeclaredMethods())
                .sorted(byOrderComparator)
                .filter(isComponentMethod)
                .toList();
    }

    private void processComponentMethod(Method method, Object configInstance) {
        String componentName = method.getAnnotation(COMPONENT_ANNOTATION_CLASS).name();
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
            appComponents.add(component);
        } catch (Exception e) {
            throw new AppComponentCreationException(
                    String.format(
                            "Ошибка создания компонента: '%s' Вызываемый метод: '%s'.",
                            componentName, method.getName()),
                    e);
        }
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
        Predicate<Object> isAssignableComponent = (c) -> componentClass.isAssignableFrom(c.getClass());
        List<Object> foundComponents =
                appComponents.stream().filter(isAssignableComponent).toList();

        if (foundComponents.isEmpty()) {
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
