import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MyDispenserTest {

    private Dispenser dispenser;

    @BeforeEach
    void setUp() {
        List<Cassette> cassettes = new ArrayList<>();
        cassettes.add(new MyCassette(100, 10)); // 1_000
        cassettes.add(new MyCassette(500, 10)); // 5_000
        cassettes.add(new MyCassette(1000, 20)); // 20_000
        cassettes.add(new MyCassette(2000, 20)); // 40_000
        cassettes.add(new MyCassette(5000, 20)); // 100_000
        dispenser = new MyDispenser(cassettes);
    }

    @Test
    @DisplayName("Выдача определённого количества")
    void testDispense_ExactAmount() throws Exception {
        Map<Integer, Integer> dispensed = dispenser.dispense(2000);
        assertEquals(1, dispensed.get(2000));
        assertNull(dispensed.get(100));
        assertNull(dispensed.get(500));
        assertNull(dispensed.get(1000));
    }

    @Test
    @DisplayName("Выдача суммы разными купюрами")
    void testDispense_MultipleDenominations() throws Exception {
        Map<Integer, Integer> dispensed = dispenser.dispense(1600);
        assertEquals(1, dispensed.get(1000));
        assertEquals(1, dispensed.get(500));
        assertEquals(1, dispensed.get(100));
    }

    @Test
    @DisplayName("Запрошенная сумма превышает остаток")
    void testDispense_NotEnoughMoneyException() {
        assertThrows(NotEnoughMoneyException.class, () -> dispenser.dispense(500_000));
    }

    @Test
    @DisplayName("Выдача возможна")
    void testCanDispense_True() {
        assertTrue(dispenser.canDispense(1600));
    }

    @Test
    @DisplayName("Выдача невозможна")
    void testCanDispense_False() {
        assertFalse(dispenser.canDispense(10));
    }
}
