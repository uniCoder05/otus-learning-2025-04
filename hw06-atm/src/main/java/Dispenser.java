import java.util.List;
import java.util.Map;

public interface Dispenser {

    Map<Integer, Integer> dispense(int amount) throws Exception;

    boolean canDispense(int amount);

    List<? extends Cassette> getCassettes();
}
