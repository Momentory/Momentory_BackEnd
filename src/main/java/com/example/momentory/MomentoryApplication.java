package com.example.momentory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MomentoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MomentoryApplication.class, args);
	}

}
