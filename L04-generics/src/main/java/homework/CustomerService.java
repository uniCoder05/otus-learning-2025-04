package homework;

import java.util.*;

public class CustomerService {

    private final NavigableMap<Customer, String> customers =
            new TreeMap<>(Comparator.comparingLong(Customer::getScores));

    public Map.Entry<Customer, String> getSmallest() {
        var key = customers.firstKey();
        return new AbstractMap.SimpleEntry<>(copyOf(key), customers.get(key));
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        var key = customers.higherKey(customer);
        return key != null ? new AbstractMap.SimpleEntry<>(copyOf(key), customers.get(key)) : null;
    }

    public void add(Customer customer, String data) {
        customers.put(customer, data);
    }

    private Customer copyOf(Customer customer) {
        return new Customer(customer.getId(), customer.getName(), customer.getScores());
    }
}
