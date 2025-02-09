package com.siddhu.spring.customer;

import com.siddhu.spring.AbstractTestContainers;
import com.siddhu.spring.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.sql.Date;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class})
class CustomerRepositoryTest extends AbstractTestContainers {

    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        System.out.println(applicationContext.getBeanDefinitionCount());
    }

    @Test
    void existsCustomerByName() {
        //Given
        var random = new Random();
        String[] gender = {"male","female"};
        Customer customer = new Customer(
                FAKER.name().fullName(),
                "password", new Date(FAKER.date().birthday().getTime()),
                gender[random.nextInt(2)]
        );
        underTest.save(customer);
        
        //When
        var actual = underTest.existsCustomerByName(customer.getName());

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByNameFailsWhenNameNotPresent() {
        //Given
        String name = FAKER.name().fullName();

        //When
        var actual = underTest.existsCustomerByName(name);

        //Then
        assertThat(actual).isFalse();
    }
}