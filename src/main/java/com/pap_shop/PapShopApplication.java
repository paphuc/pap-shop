package com.pap_shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PapShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(PapShopApplication.class, args);
	}

}
