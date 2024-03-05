package com.siddhu.spring;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest
class ApplicationTests extends AbstractTestContainers{

//	@Autowired
//	private ApplicationContext applicationContext;


	@Test
	void canStartPostgresDB() {
		assertThat(postgreSQLContainer.isRunning()).isTrue();
		assertThat(postgreSQLContainer.isCreated()).isTrue();
//		assertThat(postgreSQLContainer.isHealthy()).isTrue();

	}

//	@Test
//	void canApplyDbMigrationsWithFlyway() {
//    ...the code here is refactored to AbstractTestContainersClass
//		System.out.println(applicationContext.getBeanDefinitionCount());
//		for (String beanDefinitionName : applicationContext.getBeanDefinitionNames()) {
//			System.out.println(beanDefinitionName);
//		}
//		System.out.println();
//	}


}
