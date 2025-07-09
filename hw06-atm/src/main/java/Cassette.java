import java.util.List;

public interface Cassette {

    int getDenomination();

    int getCount();

    void add(int amount);

    List<Integer> withdraw(int amount) throws Exception;
}
