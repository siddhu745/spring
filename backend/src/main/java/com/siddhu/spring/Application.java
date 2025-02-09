package com.siddhu.spring;

import com.github.javafaker.Faker;
import com.siddhu.spring.customer.Customer;
import com.siddhu.spring.customer.CustomerRepository;
import com.siddhu.spring.s3.S3Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class Application {



	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);

	}

	@Bean
	CommandLineRunner runner(
			CustomerRepository customerRepository,
			PasswordEncoder passwordEncoder,
			S3Service s3Service) {
		return args -> {
			s3Service.putObject(
					"sb-practice",
					"foo",
					"hello world".getBytes()
			);

			byte[] obj = s3Service.getObject(
					"sb-practice",
					"foo"
			);

			System.out.println("Hooray " + new String(obj));
//			createRandomCustomer(customerRepository, passwordEncoder);
		};
	}

	private static void createRandomCustomer(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
		var faker = new Faker();
		var random = new Random();
		String[] gender = {"male","female"};
//			System.out.println();
//			for(int i = 0; i < 3; i++) {
		Customer customer = new Customer(
				faker.name().fullName(),
passwordEncoder.encode("password"),
				new Date(faker.date().birthday().getTime()),
				gender[random.nextInt(2)]
		);

		customerRepository.save(customer);
	}


}
