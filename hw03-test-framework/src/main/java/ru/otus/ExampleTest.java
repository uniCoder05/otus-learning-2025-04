package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotation.After;
import ru.otus.annotation.Before;
import ru.otus.annotation.Test;

public class ExampleTest {

    private final Logger logger = LoggerFactory.getLogger(ExampleTest.class);
    private String setupData;

    @Before
    public void setup() {
        setupData = "проинициализированы";
        logger.info("Before: установочные данные {}", setupData);
    }

    @Test
    public void test1() {
        logger.info("Test 1: установочные данные {}", setupData);
        assert setupData != null;
    }

    @Test
    public void test2() {
        logger.info("Test 2: данный тест должен быть провален");
        throw new RuntimeException("Тест провален специально");
    }

    @Test
    public void test3() {
        logger.info("Test 3: установочные данные {}", setupData);
        assert setupData != null;
    }

    @After
    public void tearDown() {
        setupData = "очищены";
        logger.info("After: установочные данные {}", setupData);
    }
}
