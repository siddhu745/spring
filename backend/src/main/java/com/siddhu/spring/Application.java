package com.siddhu.spring;

import com.github.javafaker.Faker;
import com.siddhu.spring.customer.Customer;
import com.siddhu.spring.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Date;
import java.util.Random;

@SpringBootApplication
public class Application {



	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);

	}

	@Bean
	CommandLineRunner runner(CustomerRepository customerRepository) {
		return args -> {
			var faker = new Faker();
			var random = new Random();
			String[] gender = {"male","female"};
//			System.out.println();
//			for(int i = 0; i < 3; i++) {
				Customer customer = new Customer(
						faker.name().fullName(),
						new Date(faker.date().birthday().getTime()),
						gender[random.nextInt(2)]
				);

				customerRepository.save(customer);
//			}
		};
	}


}
