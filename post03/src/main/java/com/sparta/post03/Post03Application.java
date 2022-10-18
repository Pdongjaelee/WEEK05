package com.sparta.post03;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Post03Application {

	public static void main(String[] args) {

		SpringApplication.run(Post03Application.class, args);
	}

}
