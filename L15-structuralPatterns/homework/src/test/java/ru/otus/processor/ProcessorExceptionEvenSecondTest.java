package ru.otus.processor;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.otus.model.Message;
import ru.otus.processor.homework.DateTimeProvider;
import ru.otus.processor.homework.EvenSecondException;
import ru.otus.processor.homework.ProcessorExceptionEvenSecond;

public class ProcessorExceptionEvenSecondTest {

    private final String MSG = EvenSecondException.DEFAULT_MSG;
    private final LocalDateTime localDateTime = LocalDateTime.of(2025, 5, 23, 15, 15, 0);
    private Message message;

    @BeforeEach
    void Setup() {
        message = new Message.Builder(1L).field1("test message").build();
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 20, 30, 40, 50})
    @DisplayName("Процессор выбрасывает исключение в четную секунду")
    void shouldThrowExceptionOnEvenSecond(int evenSec) {
        // given
        var evenSecondTime = localDateTime.withSecond(evenSec);
        DateTimeProvider dateTimeProvider = () -> evenSecondTime;

        var processor = new ProcessorExceptionEvenSecond(dateTimeProvider);

        // when & then
        assertThatExceptionOfType(EvenSecondException.class)
                .isThrownBy(() -> processor.process(message))
                .withMessage(MSG + evenSec);
    }

    @ParameterizedTest
    @ValueSource(ints = {11, 21, 31, 41, 51})
    @DisplayName("Процессор обрабатывает сообщение в нечетную секунду")
    void shouldProcessMessageOnOddSecond(int oddSec) {
        // given
        var oddSecondTime = localDateTime.withSecond(oddSec);
        DateTimeProvider dateTimeProvider = () -> oddSecondTime;

        var processor = new ProcessorExceptionEvenSecond(dateTimeProvider);

        // when
        var result = processor.process(message);

        // then
        assertThat(result).isEqualTo(message);
    }

    @RepeatedTest(5)
    @DisplayName("Процессор обрабатывает сообщение в зависимости от текущего системного времени")
    void shouldWorkWithSystemTime(TestInfo testInfo) {
        // given
        var processor = new LoggerProcessor(new ProcessorExceptionEvenSecond());

        try {
            var result = processor.process(message);
            assertThat(result).isEqualTo(message);
        } catch (EvenSecondException e) {
            assertThat(e.getMessage()).contains(MSG);
        }
    }
}
