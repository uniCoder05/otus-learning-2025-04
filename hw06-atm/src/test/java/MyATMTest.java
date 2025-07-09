import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MyATMTest {

    private ATM atm;
    private int startBalance;

    @BeforeEach
    void setUp() {
        List<Cassette> cassettes = Arrays.asList(
                new MyCassette(100, 10), // 1_000
                new MyCassette(500, 4), // 2_000
                new MyCassette(1000, 3), // 3_000
                new MyCassette(2000, 2), // 4_000
                new MyCassette(5000, 1) // 5_000
                );
        startBalance = cassettes.stream()
                .mapToInt(c -> c.getCount() * c.getDenomination())
                .sum();
        Dispenser dispenser = new MyDispenser(cassettes);

        atm = new MyATM(dispenser);
    }

    @Test
    void testDeposit_ValidDenomination() throws Exception {
        int denomination = 100;
        int amount = 5;
        atm.deposit(denomination, amount);
        assertThat(atm.getBalance()).isEqualTo(startBalance + denomination * amount);
    }

    @Test
    void testDeposit_InvalidDenomination() {
        int invalidDenomination = 11;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> atm.deposit(invalidDenomination, 1));
        assertThat(exception.getMessage()).isEqualTo("Неверный номинал купюры: " + invalidDenomination);
    }

    @Test
    void testWithdraw_SuccessfulWithdrawal() throws Exception {
        Map<Integer, Integer> result = atm.withdraw(300);
        assertThat(result).containsEntry(100, 3);
        assertThat(atm.getBalance()).isEqualTo(startBalance - 300);
    }

    @Test
    void testWithdraw_NotEnoughMoney() {
        Exception exception = assertThrows(NotEnoughMoneyException.class, () -> atm.withdraw(startBalance + 500));
        assertThat(exception.getMessage()).isEqualTo("Запрашиваемая сумма превышает баланс");
    }

    @Test
    void testWithdraw_CannotDispenseExactAmount() {
        Exception exception = assertThrows(NotEnoughMoneyException.class, () -> atm.withdraw(123));
        assertThat(exception.getMessage()).isEqualTo("Запрашиваемая сумма не может быть выдана имеющимися купюрами");
    }

    @Test
    void testGetBalance() {
        assertThat(atm.getBalance()).isEqualTo(startBalance);
    }
}
