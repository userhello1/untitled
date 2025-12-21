package com.livecoding.billingservice.service;

import com.livecoding.billingservice.dto.ProductSelection;
import com.livecoding.events.BillCreatedEvent;
import com.livecoding.billingservice.dto.Customer;
import com.livecoding.billingservice.dto.Product;
import com.livecoding.billingservice.entites.Bill;
import com.livecoding.billingservice.entites.ProductItem;
import com.livecoding.billingservice.feing.CustomerRestClient;
import com.livecoding.billingservice.feing.ProductRestClient;
import com.livecoding.billingservice.kafka.BillEventProducer;
import com.livecoding.billingservice.repository.BillRepository;
import com.livecoding.billingservice.repository.ProductItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class BillingService {

    private final BillRepository billRepository;
    private final ProductItemRepository productItemRepository;
    private final CustomerRestClient customerRestClient;
    private final ProductRestClient productRestClient;
    private final BillEventProducer billEventProducer;

    @Autowired
    public BillingService(
            BillRepository billRepository,
            ProductItemRepository productItemRepository,
            CustomerRestClient customerRestClient,
            ProductRestClient productRestClient,
            BillEventProducer billEventProducer
    ) {
        this.billRepository = billRepository;
        this.productItemRepository = productItemRepository;
        this.customerRestClient = customerRestClient;
        this.productRestClient = productRestClient;
        this.billEventProducer = billEventProducer;
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Bill getBillById(Long id) {
        Bill bill = billRepository.findById(id).orElse(null);
        if (bill != null) {
            bill.setCustomer(customerRestClient.findCustomerById(bill.getCustomerId()));
        }
        return bill;
    }

    public List<ProductItem> getBillItems(Long billId) {
        Bill bill = billRepository.findById(billId).orElse(null);
        if (bill != null) {
            List<ProductItem> items = productItemRepository.findByBill(bill);
            items.forEach(item -> {
                item.setProduct(productRestClient.findProductById(item.getProductId()));
            });
            return items;
        }
        return Collections.emptyList();
    }

    public String generateAllBills() {
        List<Customer> customers = customerRestClient.getAllCustomers();
        List<Product> products = productRestClient.getAllProducts();

        customers.forEach(customer -> {
            Bill bill = Bill.builder()
                    .billingDate(new Date())
                    .customerId(customer.getId())
                    .build();
            billRepository.save(bill);

            products.forEach(product -> {
                ProductItem productItem = ProductItem.builder()
                        .bill(bill)
                        .productId(product.getId())
                        .quantity(1 + (int)(Math.random() * 10))
                        .unitPrice(product.getPrice())
                        .build();
                productItemRepository.save(productItem);
            });
        });

        return "✅ " + billRepository.count() + " factures générées avec succès!";
    }

    public Bill createBillForCustomer(Long customerId, List<ProductSelection> selections) {
        Customer customer = customerRestClient.findCustomerById(customerId);
        if (customer == null) {
            throw new RuntimeException("Customer not found with id: " + customerId);
        }

        Bill bill = Bill.builder()
                .billingDate(new Date())
                .customerId(customerId)
                .build();
        billRepository.save(bill);

        int totalItems = 0;
        double totalAmount = 0.0;

        for (ProductSelection selection : selections) {
            Product product = productRestClient.findProductById(selection.getProductId());
            if (product != null) {
                int quantity = selection.getQuantity();
                double itemTotal = quantity * product.getPrice();

                ProductItem productItem = ProductItem.builder()
                        .bill(bill)
                        .productId(product.getId())
                        .quantity(quantity)
                        .unitPrice(product.getPrice())
                        .build();
                productItemRepository.save(productItem);

                totalItems += quantity;
                totalAmount += itemTotal;
            }
        }

        BillCreatedEvent event = BillCreatedEvent.builder()
                .billId(bill.getId())
                .customerId(customer.getId())
                .customerName(customer.getName())
                .customerEmail(customer.getEmail())
                .billingDate(bill.getBillingDate())
                .totalItems(totalItems)
                .totalAmount(totalAmount)
                .build();
        billEventProducer.sendBillCreatedEvent(event);

        bill.setCustomer(customer);
        return bill;
    }
}