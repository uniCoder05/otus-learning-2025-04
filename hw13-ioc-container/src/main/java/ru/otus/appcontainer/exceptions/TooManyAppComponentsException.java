package ru.otus.appcontainer.exceptions;

public class TooManyAppComponentsException extends RuntimeException {
    public TooManyAppComponentsException(String message) {
        super(message);
    }
}
