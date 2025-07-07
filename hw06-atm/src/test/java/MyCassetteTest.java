import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class MyCassetteTest {

    @DisplayName("Проверка остатка купюр после выдачи")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    void testWithdraw_validCountAfterWithdraw(int amount) throws Exception {
        int startCount = 10;
        int denomination = 500;
        Cassette cassette = new MyCassette(denomination, startCount);
        cassette.withdraw(amount);
        assertThat(cassette.getCount()).isEqualTo(startCount - amount);
    }

    @DisplayName("Проверка количества выдаваемых купюр")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    void testWithdraw_Withdraw(int amount) throws Exception {
        int startCount = 5;
        int denomination = 1000;
        Cassette cassette = new MyCassette(denomination, startCount);
        List<Integer> withdraw = cassette.withdraw(amount);
        assertThat(withdraw.size()).isEqualTo(amount);
    }

    @Test
    @DisplayName("Проверка выдачи количества купюр больше остатка")
    void testWithdraw_invalidAmount() {
        int denomination = 500;
        int startAmount = 10;
        int withdrawAmount = startAmount + 1;
        Cassette cassette = new MyCassette(denomination, startAmount);
        Exception exception = assertThrows(NotEnoughMoneyException.class, () -> cassette.withdraw(withdrawAmount));
        assertThat(exception.getMessage()).isEqualTo("Запрашиваемая сумма не может быть выдана имеющимися купюрами");
    }
}
