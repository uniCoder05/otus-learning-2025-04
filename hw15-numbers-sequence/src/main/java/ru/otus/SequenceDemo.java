package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequenceDemo {
    private static final Logger log = LoggerFactory.getLogger(SequenceDemo.class);

    private static final int MIN = 1;
    private static final int MAX = 10;
    private static final long STARTER_ID = 1L;
    private static boolean ascending = true;

    private int counter = 0;
    private long waitingId = 2;

    public static void main(String[] args) {
        SequenceDemo sequenceDemo = new SequenceDemo();
        new Thread(() -> sequenceDemo.action(1), "Поток-1").start();
        new Thread(() -> sequenceDemo.action(2), "Поток-2").start();
    }

    private synchronized void action(long id) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                while (id == waitingId) {
                    this.wait();
                }
                if (id == STARTER_ID) {
                    changeCounter();
                }
                log.info("{}: {}", Thread.currentThread().getName(), counter);
                waitingId = id;
                sleep();
                notifyAll();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Для последовательности чисел от MIN до MAX и обратно
    private void changeCounter() {
        if (ascending) {
            counter++;
            ascending = counter != MAX;
        } else {
            counter--;
            ascending = counter == MIN;
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
