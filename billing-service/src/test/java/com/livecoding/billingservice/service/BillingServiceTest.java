package com.livecoding.billingservice.service;

import com.livecoding.billingservice.entites.Bill;
import com.livecoding.billingservice.entites.ProductItem;
import com.livecoding.billingservice.feing.CustomerRestClient;
import com.livecoding.billingservice.feing.ProductRestClient;
import com.livecoding.billingservice.repository.BillRepository;
import com.livecoding.billingservice.repository.ProductItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private ProductItemRepository productItemRepository;

    @InjectMocks
    private BillingService billingService;

    @Mock
    private CustomerRestClient customerRestClient;

    @Mock
    private ProductRestClient productRestClient;

    @Test
    void testGetAllBills() {
        // Arrange
        Bill bill = Bill.builder()
                .id(1L)
                .billingDate(new Date())
                .customerId(1L)
                .build();

        when(billRepository.findAll()).thenReturn(Arrays.asList(bill));

        // Act
        List<Bill> result = billingService.getAllBills();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(billRepository).findAll();
    }

    @Test
    void testGetBillById() {
        // Arrange
        Bill bill = Bill.builder()
                .id(1L)
                .billingDate(new Date())
                .customerId(1L)
                .build();

        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));

        // Act
        Bill result = billingService.getBillById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(billRepository).findById(1L);
    }

    @Test
    void testGetBillById_NotFound() {
        // Arrange
        when(billRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Bill result = billingService.getBillById(99L);

        // Assert
        assertNull(result);
        verify(billRepository).findById(99L);
    }

    @Test
    void testGetBillItems() {
        // Arrange
        Bill bill = Bill.builder()
                .id(1L)
                .billingDate(new Date())
                .customerId(1L)
                .build();

        ProductItem item = ProductItem.builder()
                .id(1L)
                .bill(bill)
                .productId(1L)
                .quantity(2)
                .unitPrice(99.99)
                .build();

        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));
        when(productItemRepository.findByBill(bill)).thenReturn(Arrays.asList(item));

        // Act
        List<ProductItem> result = billingService.getBillItems(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(billRepository).findById(1L);
        verify(productItemRepository).findByBill(bill);
    }
}