package com.livecoding.customerservice;

import com.livecoding.customerservice.entities.Customer;
import com.livecoding.customerservice.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(CustomerRepository customerRepository) {
        return args -> {
            customerRepository.save(Customer.builder().name("John Doe").email("m@gm.com").build());
            customerRepository.save(Customer.builder().name("Imen").email("m1@gm.com").build());
            customerRepository.save(Customer.builder().name("Yassin").email("m5@gm.com").build());

    };}
}
