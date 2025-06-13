package ru.otus;

public class TestLogging implements TestLoggingInterface {

    @Log
    @Override
    public void calculation(int param1) {
        System.out.println("run calculation with param1");
    }

    @Log
    @Override
    public void calculation(int param1, int param2) {
        System.out.println("run calculation with param1, param2");
    }

    @Log
    @Override
    public void calculation(int param1, int param2, String param3) {
        System.out.println("run calculation with param1, param2, param3");
    }

    @Override
    public void calculation(String s) {
        System.out.println("run calculation with param1 String -- " + s);
    }
}
