package ru.otus.appcontainer.exceptions;

public class AppComponentCreationException extends RuntimeException {
    public AppComponentCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppComponentCreationException(String message) {
        super(message);
    }
}
