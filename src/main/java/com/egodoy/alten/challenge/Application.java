package com.egodoy.alten.challenge;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EntityScan(basePackages = {"com/egodoy/alten/challenge/model"})
@EnableAutoConfiguration
public class Application {

	@Value("${info.version}")
	private String appVersion;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public OpenAPI apiInfo()
	{
		return new OpenAPI()
				.info(new Info().title( "Alten Challenge" )
						.description( "API for Post-Covid Hotel Scenario")
						.license( new License().name("Eladio Godoy").url("https://www.linkedin.com/in/eladio-godoy-284125128/"))
						.contact( new Contact().email( "eladiogodoy328@gmail.com") )
						.version(appVersion));
	}

}
