import java.util.Map;

public interface ATM {

    void deposit(int denomination, int amount) throws Exception;

    Map<Integer, Integer> withdraw(int amount) throws Exception;

    long getBalance();

    void printTechInfo();
}
