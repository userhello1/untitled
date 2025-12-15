package com.livecoding.inventoryservice;

import com.livecoding.inventoryservice.entities.Product;
import com.livecoding.inventoryservice.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(ProductRepository productRepository) {
        return args -> {
            productRepository.save(Product.builder().name("x1").price(1.2).quantity(2).build());
            productRepository.save(Product.builder().name("x5").price(122).quantity(2).build());
            productRepository.save(Product.builder().name("x2").price(12).quantity(25).build());
            productRepository.save(Product.builder().name("x6").price(600).quantity(2).build());

        };}
}
