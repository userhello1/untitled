package com.livecoding.inventoryservice.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Product {
    @Id 
    @GeneratedValue
    @JsonProperty("id")
    private Long id;
    private String name;
    private double price;
    private int quantity;
}
