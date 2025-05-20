package homework;

import java.util.ArrayDeque;
import java.util.Deque;

public class CustomerReverseOrder {

    private final Deque<Customer> customersList = new ArrayDeque<>();

    public void add(Customer customer) {
        customersList.add(customer);
    }

    public Customer take() {
        return customersList.pollLast();
    }
}
