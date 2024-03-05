package com.siddhu.spring.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class CustomerJpaDataAccessServiceTest {

    private CustomerJpaDataAccessService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJpaDataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void getCustomers() {
        //When
        underTest.getCustomers();

        //Then
        verify(customerRepository)
                .findAll();
    }

    @Test
    void getCustomer() {
        //Given
        int id = 1;

        //When
        underTest.getCustomer(id);

        //Then
        verify(customerRepository)
                .findById(id);
    }

    @Test
    void insertCustomer() {
        //Given
        Customer customer = null;

        //When
        underTest.insertCustomer(customer);

        //Then
        verify(customerRepository)
                .save(customer);
    }

    @Test
    void existsPersonWithId() {
        //Given
        int id = 1;

        //When
        underTest.existsPersonWithId(id);

        //Then
        verify(customerRepository)
                .existsById(id);
    }

    @Test
    void existsPersonWithName() {
        //Given
        String name = "siddhuBouy";

        //When
        underTest.existsPersonWithName(name);

        //Then
        verify(customerRepository)
                .existsCustomerByName(name);
    }

    @Test
    void deleteCustomer() {
        //Given
        int id = 1;

        //When
        underTest.deleteCustomer(id);

        //Then
        verify(customerRepository)
                .deleteById(id);
    }

    @Test
    void updateCustomer() {
        //Given
        Customer update = null;

        //When
        underTest.updateCustomer(update);

        //Then
        verify(customerRepository)
                .save(update);
    }
}