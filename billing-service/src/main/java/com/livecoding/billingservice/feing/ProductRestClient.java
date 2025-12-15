package com.livecoding.billingservice.feing;

import com.livecoding.billingservice.dto.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "inventory-service")
public interface ProductRestClient {
    @GetMapping("/api/products/{id}")
    Product findProductById(@PathVariable Long id);

    @GetMapping("/api/products")
    List<Product> getAllProducts();
}

// open feign permet de faire des appels rest de maniere declarative
// il appel directement les services via leur nom sans passer par l'url grace a eureka