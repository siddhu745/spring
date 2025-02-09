package com.siddhu.spring.journey;

import com.github.javafaker.Faker;
import com.siddhu.spring.auth.AuthenticationRequest;
import com.siddhu.spring.auth.AuthenticationResponse;
import com.siddhu.spring.customer.CustomerDTO;
import com.siddhu.spring.customer.CustomerRegistrationRequest;
import com.siddhu.spring.jwt.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationIT {
    private static final String CUSTOMER_URI = "api/v1/customers";
    private static final String AUTHENTICATION_URI = "api/v1/auth";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JWTUtil jwtUtil;

    @Test
    void canLogin() {
        String[] genderList = {"male","female","LGBTQ+"};
        Random random  = new Random();
        Faker faker = new Faker();
        String fakerName = faker.name().fullName();
        Date fakerDate = new Date(faker.date().birthday().getTime());
        String fakerGender = genderList[random.nextInt(3)];
        String password = "password";
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                fakerName, password, fakerDate,fakerGender
        );

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                fakerName, password
        );

        webTestClient.post()
                .uri(AUTHENTICATION_URI + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        EntityExchangeResult<AuthenticationResponse> result = webTestClient.post()
                .uri(AUTHENTICATION_URI + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
                })
                .returnResult();

        String token = result.getResponseHeaders().get(AUTHORIZATION).get(0);
        AuthenticationResponse responseBody = result.getResponseBody();


        assert responseBody != null;
        CustomerDTO customerDTO = responseBody.customerDTO();
        assertThat(jwtUtil.isTokenValid(
                token,
                customerDTO.username()
                )
        ).isTrue();

        assertThat(customerDTO.name()).isEqualTo(fakerName);
        assertThat(customerDTO.gender()).isEqualTo(fakerGender);
        assertThat(customerDTO.username()).isEqualTo(fakerName);
        assertThat(customerDTO.roles()).isEqualTo(List.of("ROLE_USER"));

    }
}
