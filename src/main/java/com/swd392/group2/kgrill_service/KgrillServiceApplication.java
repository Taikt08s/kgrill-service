package com.swd392.group2.kgrill_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.modelmapper.ModelMapper;


@SpringBootApplication
@EntityScan(basePackages = "com.swd392.group2.kgrill_model.model")
@ComponentScan(basePackages = {"com.swd392.group2.kgrill_model"})
public class KgrillServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(KgrillServiceApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
