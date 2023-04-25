package com.eikona.tech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.eikona.tech")
@EntityScan(basePackages = {"com.eikona.tech.entity", "com.eikona.tech.service"})
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages ={"com.eikona.tech.repository"},
									repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
public class EmployeeImageUploadApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeImageUploadApplication.class, args);
	}

}
