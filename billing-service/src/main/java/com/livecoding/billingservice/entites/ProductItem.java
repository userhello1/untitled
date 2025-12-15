package com.livecoding.billingservice.entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.livecoding.billingservice.dto.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;
    @JsonProperty("productId")
    private Long productId;
    @ManyToOne
    @JsonIgnore
    private Bill bill;
    private int quantity;
    private double unitPrice;
    @Transient
    private Product product;
}
