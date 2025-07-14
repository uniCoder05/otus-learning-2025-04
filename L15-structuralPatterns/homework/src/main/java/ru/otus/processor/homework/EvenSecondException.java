package ru.otus.processor.homework;

public class EvenSecondException extends RuntimeException {
    public static final String DEFAULT_MSG =
            String.format("%s - current second is even: ", EvenSecondException.class.getSimpleName());

    public EvenSecondException(String msg) {
        super(DEFAULT_MSG + msg);
    }
}
