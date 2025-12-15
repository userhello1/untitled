package com.livecoding.billingservice.entites;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.livecoding.billingservice.dto.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;
    private Date billingDate;
    @JsonProperty("customerId")
    private Long customerId;
    @Transient 
    private Customer customer;
}
