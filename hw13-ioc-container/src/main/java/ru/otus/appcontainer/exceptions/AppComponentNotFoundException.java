package ru.otus.appcontainer.exceptions;

public class AppComponentNotFoundException extends RuntimeException {
    public AppComponentNotFoundException(String message) {
        super(message);
    }
}
