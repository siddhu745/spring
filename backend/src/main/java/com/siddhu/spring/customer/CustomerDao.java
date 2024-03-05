package com.siddhu.spring.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> getCustomers();
    Optional<Customer> getCustomer(Integer id);
    void insertCustomer(Customer customer);
    boolean existsPersonWithId(Integer id);

    boolean existsPersonWithName(String name);

    void deleteCustomer(Integer id);
    void updateCustomer(Customer update);
}
