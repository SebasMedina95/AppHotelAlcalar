package com.sebastian.springboot.hostal_alcalar.hostal_alcalar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "classpath:swagger.properties", encoding = "UTF-8")
public class HostalAlcalarApplication {

	public static void main(String[] args) {
		SpringApplication.run(HostalAlcalarApplication.class, args);
	}

}
