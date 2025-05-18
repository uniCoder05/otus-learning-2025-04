package homework;

import java.util.LinkedList;

public class CustomerReverseOrder {

    LinkedList<Customer> customersList = new LinkedList<>();

    public void add(Customer customer) {
        customersList.add(customer);
    }

    public Customer take() {
        return customersList.pollLast();
    }
}
