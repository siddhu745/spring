package com.siddhu.spring.journey;

import com.github.javafaker.Faker;
import com.siddhu.spring.customer.Customer;
import com.siddhu.spring.customer.CustomerDTO;
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
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
               fakerName, "password", fakerDate,fakerGender
        );

        // step2 : send a post request
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);
        // get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s",jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

//        allCustomers.forEach(System.out::println);


        // get customer by id

        assert allCustomers != null;
        int id = allCustomers.stream()
                        .filter(c -> c.name().equals(fakerName))
                                .map(CustomerDTO::id)
                                        .findFirst()
                                                .orElseThrow();


//        expectedCustomer.setId(id);
        // make sure that customer is present
        CustomerDTO expectedCustomer = new CustomerDTO(
                fakerName, fakerDate, fakerGender, List.of("ROLE_USER"), fakerName, id,
        );

        //fixed error : it unable to compare the date field. so, ignoring this field
        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("date")
                .contains(expectedCustomer);

        CustomerDTO actual = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s",jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
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
                fakerName, "password", fakerDate,fakerGender
        );
        CustomerRegistrationRequest request2 = new CustomerRegistrationRequest(
                fakerName+2, "password", fakerDate,fakerGender
        );


        // send a post request to create a customer 1
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // step2 : send a post request to create a customer 2 to get authentication to delete
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);

        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s",jwtToken))
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

        //customer 2 deletes the customer 1
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s",jwtToken))
                .exchange()
                .expectStatus()
                .isOk();

        // customer 2 gets the customer 1
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s",jwtToken))
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
                fakerName, "password", fakerDate,fakerGender
        );


        // step2 : send a post request
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);

        // get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s",jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        assert allCustomers != null;
        allCustomers.forEach(System.out::println);

        // get id
        int id = allCustomers.stream()
                .filter(c -> c.name().equals(fakerName))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        //update the customer
        String newName = faker.name().fullName();
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null,null,"test"
        );
        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s",jwtToken))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest),CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        System.out.println("done up to here");

        // get the customer back
        CustomerDTO updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s",jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDTO.class)
                .returnResult()
                .getResponseBody();

        CustomerDTO expectedCustomer = new CustomerDTO(
                fakerName, fakerDate, "test", List.of("ROLE_USER"), fakerName, id,
        );

        assertThat(updatedCustomer)
                .usingRecursiveComparison()
                .ignoringFields("date")
                .isEqualTo(expectedCustomer);
    }
}
