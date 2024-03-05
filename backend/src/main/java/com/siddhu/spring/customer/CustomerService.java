package com.siddhu.spring.customer;

import com.siddhu.spring.exceptions.DuplicateResourceException;
import com.siddhu.spring.exceptions.RequestValidationException;
import com.siddhu.spring.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerDao customerDao;


    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    List<Customer> getCustomers() {
        return customerDao.getCustomers();
    }

    Customer getCustomer(Integer id) {
        return customerDao.getCustomer(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Customer with id %s not found".formatted(id)
                )
        );
    }

    public void addCustomer(CustomerRegistrationRequest request) {
        //check is email exists
        String name = request.name();
        if(customerDao.existsPersonWithName(name)){
            throw new DuplicateResourceException(
                    "name already taken"
            );
        }

        Customer customer = new Customer(
                request.name(),
                request.date(),
                request.gender()
        );

        //add new customer
       customerDao.insertCustomer(customer);
    }

    public void removeCustomerById(Integer id) {
        if(!customerDao.existsPersonWithId(id)) {
            throw new ResourceNotFoundException(
                    "customer with id %s not found".formatted(id)
            );

        }
        customerDao.deleteCustomer(id);
    }

    public void updateCustomer(Integer id, CustomerUpdateRequest customerUpdateRequest) {
        Customer customer = getCustomer(id);
        boolean changes = false;

        if(customerUpdateRequest.name() != null && !customerUpdateRequest.name().equals(customer.getName())){
            if(customerDao.existsPersonWithName(customerUpdateRequest.name())) {
                throw new DuplicateResourceException(
                        "customer with %s already exists".formatted(customerUpdateRequest.name())
                );
            }
            customer.setName(customerUpdateRequest.name());
            changes = true;
        }

        if(customerUpdateRequest.date() != null && !customerUpdateRequest.date().toString().equals(customer.getDate().toString())){
            customer.setDate(customerUpdateRequest.date());
            changes = true;
        }

        if(customerUpdateRequest.gender() != null && !customerUpdateRequest.gender().equals(customer.getGender())){
            customer.setGender(customerUpdateRequest.gender());
            changes = true;
        }
        if(!changes) {
            throw new RequestValidationException("no data changes found");
        }

        customerDao.updateCustomer(customer);

    }
}
