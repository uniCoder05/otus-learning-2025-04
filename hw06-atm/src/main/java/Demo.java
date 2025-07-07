import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Demo {
    private static final Logger logger = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {
        int[] denominations = {100, 200, 500, 1000, 2000, 5000};
        List<Cassette> cassettes = new ArrayList<>();
        for (int denomination : denominations) {
            cassettes.add(new MyCassette(denomination, getRandom(50, 200)));
        }

        Dispenser dispenser = new MyDispenser(cassettes);
        ATM atm = new MyATM(dispenser);
        atm.printTechInfo();
        logger.info("Стартовый баланс: {} руб.", String.format("%,d", atm.getBalance()));
        int withdrawAmount = 12_500;

        try {
            atm.deposit(5000, 2);
            logger.info("Баланс после пополнения: {} руб.", String.format("%,d", atm.getBalance()));
            logger.info("Сумма для снятия: {} руб.", String.format("%,d", withdrawAmount));
            atm.withdraw(withdrawAmount);
            logger.info("Баланс после снятия: {} руб.", String.format("%,d", atm.getBalance()));
            atm.deposit(50, 1);
        } catch (Exception e) {
            logger.info("{}", e.getMessage());
        }

        try {
            withdrawAmount = (int) atm.getBalance() + 500;
            logger.info("Сумма для снятия: {} руб.", String.format("%,d", withdrawAmount));
            atm.withdraw(withdrawAmount);
        } catch (Exception e) {
            logger.info("{}", e.getMessage());
        }
    }

    static int getRandom(int min, int max) {
        Random rnd = new Random();
        return rnd.nextInt(max + 1 - min) + min;
    }
}
