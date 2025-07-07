import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyATM implements ATM {
    private static final Logger logger = LoggerFactory.getLogger(MyATM.class);

    private final Dispenser dispenser;
    private final Map<Integer, ? extends Cassette> cassettes;

    public MyATM(Dispenser dispenser) {
        this.dispenser = dispenser;
        this.cassettes = dispenser.getCassettes().stream().collect(Collectors.toMap(Cassette::getDenomination, v -> v));
    }

    @Override
    public void deposit(int denomination, int amount) {
        if (!cassettes.containsKey(denomination)) {
            throw new IllegalArgumentException("Неверный номинал купюры: " + denomination);
        }
        cassettes.get(denomination).add(amount);
    }

    @Override
    public Map<Integer, Integer> withdraw(int amount) throws Exception {
        if (amount > getBalance()) {
            throw new NotEnoughMoneyException("Запрашиваемая сумма превышает баланс");
        }
        return dispenser.dispense(amount);
    }

    @Override
    public long getBalance() {
        long balance = 0;
        for (Cassette cassette : cassettes.values()) {
            balance += (long) cassette.getDenomination() * cassette.getCount();
        }
        return balance;
    }

    private String getCassettesInfo() {
        final int DENOM_MAX_LENGTH = 4;
        final int COUNT_MAX_LENGTH = 5;
        final String H1 = "\n------ Cassettes ------\n";
        final String H2 = "Denom\tCount\tAmount\n";
        StringBuilder result = new StringBuilder();
        result.append(H1).append(H2);
        Set<Integer> keys = new TreeSet<>(cassettes.keySet());
        for (Integer denomination : keys) {
            String spacesAfterDenom = " ".repeat(denomination.toString().length() % DENOM_MAX_LENGTH);
            int count = cassettes.get(denomination).getCount();
            String spacesAfterCount = " ".repeat(denomination.toString().length() % COUNT_MAX_LENGTH);
            int amount = denomination * count;
            result.append(denomination)
                    .append(spacesAfterDenom)
                    .append("\t")
                    .append(cassettes.get(denomination).getCount())
                    .append(spacesAfterCount)
                    .append("\t")
                    .append(amount)
                    .append("\n");
        }
        result.append("-".repeat(H1.length() - 2)).append("\nTotal amount: ").append(getBalance());

        return result.toString();
    }

    @Override
    public void printTechInfo() {
        logger.info("{}", getCassettesInfo());
    }
}
