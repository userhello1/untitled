package com.livecoding.billingservice.repository;

import com.livecoding.billingservice.entites.Bill;
import com.livecoding.billingservice.entites.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
    List<ProductItem> findByBill(Bill bill);
}
