package com.livecoding.billingservice.feing;

import com.livecoding.billingservice.dto.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "customer-service")
public interface CustomerRestClient {
    @GetMapping("/api/customers/{id}")
    Customer findCustomerById(@PathVariable Long id);

    @GetMapping("/api/customers")
    List<Customer> getAllCustomers();
}
// open feign permet de faire des appels rest de maniere declarative
// il appel directement les services via leur nom sans passer par l'url grace a eureka