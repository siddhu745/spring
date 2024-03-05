package com.siddhu.spring.journey;

import com.github.javafaker.Faker;
import com.siddhu.spring.customer.Customer;
import com.siddhu.spring.customer.CustomerRegistrationRequest;
import com.siddhu.spring.customer.CustomerUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {
    private static final String CUSTOMER_URI = "api/v1/customers";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void canRegisterCustomer() {

        // step1 : create a registration request
        String[] genderList = {"male","female","LGBTQ+"};
        Random random  = new Random();
        Faker faker = new Faker();
        String fakerName = faker.name().fullName();
        Date fakerDate = new Date(faker.date().birthday().getTime());
        String fakerGender = genderList[random.nextInt(3)];
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
               fakerName,fakerDate,fakerGender
        );

        // step2 : send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that customer is present
        Customer expectedCustomer = new Customer (
                fakerName,fakerDate,fakerGender
        );
        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id","date")
                .contains(expectedCustomer);
        // get customer by id

        assert allCustomers != null;
        int id = allCustomers.stream()
                        .filter(c -> c.getName().equals(fakerName))
                                .map(Customer::getId)
                                        .findFirst()
                                                .orElseThrow();


        expectedCustomer.setId(id);

        Customer actual = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("date")
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {

        // step1 : create a registration request
        String[] genderList = {"male","female","LGBTQ+"};
        Random random  = new Random();
        Faker faker = new Faker();
        String fakerName = faker.name().fullName();
        Date fakerDate = new Date(faker.date().birthday().getTime());
        String fakerGender = genderList[random.nextInt(3)];
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                fakerName,fakerDate,fakerGender
        );

        // step2 : send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // get customer by id

        assert allCustomers != null;
        int id = allCustomers.stream()
                .filter(c -> c.getName().equals(fakerName))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //delete the customer

        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {

        // step1 : create a registration request
        String[] genderList = {"male","female","LGBTQ+"};
        Random random  = new Random();
        Faker faker = new Faker();
        String fakerName = faker.name().fullName();
        Date fakerDate = new Date(faker.date().birthday().getTime());
        String fakerGender = genderList[random.nextInt(3)];
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                fakerName,fakerDate,fakerGender
        );

        // step2 : send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // get id
        assert allCustomers != null;
        int id = allCustomers.stream()
                .filter(c -> c.getName().equals(fakerName))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //update the customer
        String newName = faker.name().fullName();
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName,null,null
        );
        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest),CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get the customer back
        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expectedCustomer = new Customer(
                id,newName,fakerDate,fakerGender
        );

        assertThat(updatedCustomer)
                .usingRecursiveComparison()
                .ignoringFields("date")
                .isEqualTo(expectedCustomer);
    }


}
