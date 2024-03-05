package com.siddhu.spring.customer;

import com.siddhu.spring.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomerJdbcDataAccessServiceTest extends AbstractTestContainers {

    private CustomerJdbcDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJdbcDataAccessService(
                getJDBCTemplate(),
                customerRowMapper
        );
    }

    @Test
    void getCustomers() {
        //Given
        var random = new Random();
        String[] gender = {"male","female"};
        Customer customer = new Customer(
                FAKER.name().fullName(),
                new Date(FAKER.date().birthday().getTime()),
                gender[random.nextInt(2)]
        );
        underTest.insertCustomer(customer);

        //When
        List<Customer> customers = underTest.getCustomers();

        //Then
        assertThat(customers).isNotEmpty();
    }

    @Test
    void getCustomer() {

        //Given
        var random = new Random();
        String[] gender = {"male","female"};
        Customer customer = new Customer(
                FAKER.name().fullName(),
                new Date(FAKER.date().birthday().getTime()),
                gender[random.nextInt(2)]
        );
        underTest.insertCustomer(customer);

        int id = underTest.getCustomers()
                .stream()
                .filter(c -> c.getName().equals(customer.getName()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //When
        Optional<Customer> actual = underTest.getCustomer(id);

        //Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId().equals(id));
            assertThat(c.getName().equals(customer.getName()));
            assertThat(c.getDate().equals(customer.getDate()));
            assertThat(c.getGender().equals(customer.getGender()));
        });
    }

    @Test
    void willReturnEmptyWhenFalseIdGivenToGetCustomer() {
        //Given
        int id = -1;

        //When
        var actual = underTest.getCustomer(id);

        //Then
        assertThat(actual).isEmpty();
    }

    @Test
    void existsPersonWithId() {
        //Given
        var random = new Random();
        String[] gender = {"male","female"};
        Customer customer = new Customer(
                FAKER.name().fullName(),
                new Date(FAKER.date().birthday().getTime()),
                gender[random.nextInt(2)]
        );
        underTest.insertCustomer(customer);

        int id = underTest.getCustomers()
                .stream()
                .filter(c -> c.getName().equals(customer.getName()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //When
        boolean actual = underTest.existsPersonWithId(id);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithIdReturnsFalseWhenIdDoesNotExists() {
        //Given
        int id = -1;

        //When
        boolean actual = underTest.existsPersonWithId(id);

        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsPersonWithName() {
        //Given
        var random = new Random();
        String[] gender = {"male","female"};
        Customer customer = new Customer(
                FAKER.name().fullName(),
                new Date(FAKER.date().birthday().getTime()),
                gender[random.nextInt(2)]
        );
        underTest.insertCustomer(customer);

        String name = underTest.getCustomers()
                .stream()
                .map(Customer::getName)
                .filter(cName -> cName.equals(customer.getName()))
                .findFirst()
                .orElseThrow();

        //When
        boolean actual = underTest.existsPersonWithName(name);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithNameReturnsFalseWhenDoesNotExists() {
        //Given
        String name = FAKER.name().fullName();

        //When
        boolean actual = underTest.existsPersonWithName(name);

        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomer() {
        //Given
        var random = new Random();
        String[] gender = {"male","female"};
        Customer customer = new Customer(
                FAKER.name().fullName(),
                new Date(FAKER.date().birthday().getTime()),
                gender[random.nextInt(2)]
        );
        underTest.insertCustomer(customer);

        int id = underTest.getCustomers()
                .stream()
                .filter(c -> c.getName().equals(customer.getName()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //When
        underTest.deleteCustomer(id);

        //Then
        Optional<Customer> actual = underTest.getCustomer(id);
        assertThat(actual).isNotPresent();

    }

    @Test
    void updateCustomer() {
        //Given
        var random = new Random();
        String[] gender = {"male","female"};
        Customer customer = new Customer(
                FAKER.name().fullName(),
                new Date(FAKER.date().birthday().getTime()),
                gender[random.nextInt(2)]
        );
        underTest.insertCustomer(customer);

        int id = underTest.getCustomers()
                .stream()
                .filter(c -> c.getName().equals(customer.getName()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newName = "siddhuBoyy";
        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);
        update.setDate(new Date(2002,12,28));
        update.setGender("male");

        //When
        underTest.updateCustomer(update);


        //Then
        boolean actual = underTest.existsPersonWithName(newName);
        assertThat(actual).isTrue();
    }
}