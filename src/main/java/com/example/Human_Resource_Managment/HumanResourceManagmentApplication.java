package com.example.Human_Resource_Managment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class HumanResourceManagmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(HumanResourceManagmentApplication.class, args);
	}

}
