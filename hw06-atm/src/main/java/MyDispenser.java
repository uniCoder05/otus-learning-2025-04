import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record MyDispenser(List<Cassette> cassettes) implements Dispenser {

    public MyDispenser(List<Cassette> cassettes) {
        this.cassettes = cassettes;
        this.cassettes.sort((c1, c2) -> Integer.compare(c2.getDenomination(), c1.getDenomination()));
    }

    @Override
    public Map<Integer, Integer> dispense(int amount) throws Exception {
        if (!canDispense(amount)) {
            throw new NotEnoughMoneyException(amount);
        }
        Map<Integer, Integer> dispenseNotes = new HashMap<>();
        int remAmount = amount;

        for (Cassette cassette : cassettes) {
            int denomination = cassette.getDenomination();
            int countAvailable = cassette.getCount();
            int countToUse = Math.min(countAvailable, remAmount / denomination);

            if (countToUse > 0) {
                List<Integer> withdraw = cassette.withdraw(countToUse);
                if (withdraw != null) {
                    dispenseNotes.put(denomination, countToUse);
                    remAmount -= countToUse * denomination;
                }
            }
        }

        if (remAmount != 0) {
            throw new NotEnoughMoneyException(amount);
        }

        return dispenseNotes;
    }

    @Override
    public boolean canDispense(int amount) {
        int remAmount = amount;
        for (Cassette cassette : cassettes) {
            int denomination = cassette.getDenomination();
            int countAvailable = cassette.getCount();
            int countToUse = Math.min(countAvailable, remAmount / denomination);
            remAmount -= countToUse * denomination;
        }
        return remAmount == 0;
    }

    @Override
    public List<Cassette> getCassettes() {
        return new ArrayList<>(cassettes);
    }
}
