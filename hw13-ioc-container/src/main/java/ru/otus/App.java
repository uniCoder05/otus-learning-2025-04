package ru.otus;

import ru.otus.appcontainer.AppComponentsContainerImpl;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.config.AppConfig;
import ru.otus.services.GameProcessor;

@SuppressWarnings({"squid:S125", "squid:S106"})
public class App {

    public static void main(String[] args) {
        // Опциональные варианты (optional):
        //        AppComponentsContainer container = new AppComponentsContainerImpl(AppConfig1.class, AppConfig2.class);
        //        AppComponentsContainer container = new AppComponentsContainerImpl("ru.otus.config.optional");

        // Обязательный вариант
        AppComponentsContainer container = new AppComponentsContainerImpl(AppConfig.class);

        // Приложение должно работать в каждом из указанных ниже вариантов
        //        GameProcessor gameProcessor = container.getAppComponent(GameProcessor.class);
        //        GameProcessor gameProcessor = container.getAppComponent(GameProcessorImpl.class);
        GameProcessor gameProcessor = container.getAppComponent("gameProcessor");

        gameProcessor.startGame();
    }
}
