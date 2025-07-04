package com.spring.react;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReactRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactRestApplication.class, args);
	}

}
