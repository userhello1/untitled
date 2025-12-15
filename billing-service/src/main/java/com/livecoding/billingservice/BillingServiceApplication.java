package com.livecoding.billingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableFeignClients
@EnableKafkaStreams
public class BillingServiceApplication {
	// circuit breaker with resilience4j : il permet de gerer les pannes de services en evitant les appels a des services en panne
	// il permet egalement de definir des strategies de retry, de timeout, de fallback
	// https://www.youtube.com/watch?v=-iM3J_mgqlM
    // si j'arrette un service l'autre service s'arrete egalement
    public static void main(String[] args) {
		SpringApplication.run(BillingServiceApplication.class, args);
	}
}
