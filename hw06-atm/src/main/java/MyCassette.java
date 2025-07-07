import java.util.ArrayList;
import java.util.List;

public class MyCassette implements Cassette {

    private final int denomination;
    private int count;

    public MyCassette(int denomination, int count) {
        this.denomination = denomination;
        this.count = count;
    }

    @Override
    public int getDenomination() {
        return denomination;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void add(int amount) {
        this.count += amount;
    }

    @Override
    public List<Integer> withdraw(int amount) throws NotEnoughMoneyException {
        if (amount > count) {
            throw new NotEnoughMoneyException();
        }
        this.count -= amount;
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            result.add(denomination);
        }

        return result;
    }
}
