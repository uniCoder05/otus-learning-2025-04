package ru.otus.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.ExampleTest;
import ru.otus.annotation.After;
import ru.otus.annotation.Before;
import ru.otus.annotation.Test;
import ru.otus.reflection.ReflectionHelper;

public class TestRunner {

    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    private TestRunner() {}

    public static void main(String[] args) {
        TestRunner.run(ExampleTest.class);
    }

    public static void run(Class<?> testClass) {
        TestResult result = runTests(testClass);
        printResult(result);
    }

    private static TestResult runTests(Class<?> testClass) {
        List<Method> beforeMethods = new ArrayList<>();
        List<Method> afterMethods = new ArrayList<>();
        List<Method> testMethods = new ArrayList<>();

        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Before.class)) {
                beforeMethods.add(method);
            } else if (method.isAnnotationPresent(After.class)) {
                afterMethods.add(method);
            } else if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            }
        }

        int totalTests = testMethods.size();
        int passedTests = 0;
        int failedTests = 0;

        for (Method testMethod : testMethods) {
            String testMethodName = testMethod.getName();
            Object testInstance = ReflectionHelper.instantiate(testClass);
            ReflectionHelper.callMethods(testInstance, beforeMethods);
            try {
                ReflectionHelper.callMethod(testInstance, testMethodName);
                passedTests++;
                logger.info("Тест {} пройден", testMethodName);
            } catch (Exception e) {
                failedTests++;
                logger.info(String.format("Тест {} не прошёл: %s", e.getMessage()), testMethodName);
            } finally {
                ReflectionHelper.callMethods(testInstance, afterMethods);
            }
        }

        return new TestResult(totalTests, passedTests, failedTests);
    }

    private static void printResult(TestResult result) {
        String sepLine = "-".repeat(100);
        System.out.println(sepLine);
        logger.info("Общее число запущенных тестов: {}", result.totalTests);
        logger.info("Пройдено тестов: {}", result.passedTests);
        logger.info("Провалено тестов: {}", result.failedTests);
        System.out.println(sepLine);
    }

    private static class TestResult {
        int totalTests;
        int passedTests;
        int failedTests;

        public TestResult(int totalTests, int passedTests, int failedTests) {
            this.totalTests = totalTests;
            this.passedTests = passedTests;
            this.failedTests = failedTests;
        }
    }
}
