package ru.otus;

public class Demo {
    public static void main(String[] args) {
        new Demo().action();
    }

    public void action() {
        TestLoggingInterface testLogging = MyIoc.createMyClass(TestLogging.class);
        testLogging.calculation(1);
        testLogging.calculation(1, 2);
        testLogging.calculation(1, 2, "three");
        testLogging.calculation("этот метод не будет логироваться");
    }
}
