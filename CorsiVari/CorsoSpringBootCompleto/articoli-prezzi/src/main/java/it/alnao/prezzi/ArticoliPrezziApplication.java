package it.alnao.prezzi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ArticoliPrezziApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArticoliPrezziApplication.class, args);
	}

}
