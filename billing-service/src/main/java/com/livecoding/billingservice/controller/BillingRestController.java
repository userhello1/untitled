package com.livecoding.billingservice.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/bills")
public class BillingRestController {

    @Autowired
    private BillRepository billRepository;
    
    @Autowired
    private ProductItemRepository productItemRepository;
    
    @Autowired
    private CustomerRestClient customerRestClient;
    
    @Autowired
    private ProductRestClient productRestClient;
    
    /**
     * ========================================
     * KAFKA PRODUCER
     * ========================================
     */
    @Autowired
    private BillEventProducer billEventProducer;
    
    // Récupérer toutes les factures
    @GetMapping
    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }
    
    // Récupérer une facture par ID
    @GetMapping("/{id}")
    public Bill getBillById(@PathVariable Long id) {
        Bill bill = billRepository.findById(id).orElse(null);
        if (bill != null) {
            // Récupérer les informations du customer
            bill.setCustomer(customerRestClient.findCustomerById(bill.getCustomerId()));
        }
        return bill;
    }
    
    // Récupérer les items d'une facture
    @GetMapping("/{id}/items")
    public List<ProductItem> getBillItems(@PathVariable Long id) {
        Bill bill = billRepository.findById(id).orElse(null);
        if (bill != null) {
            List<ProductItem> items = productItemRepository.findByBill(bill);
            // Enrichir chaque item avec les infos du produit
            items.forEach(item -> {
                item.setProduct(productRestClient.findProductById(item.getProductId()));
            });
            return items;
        }
        return null;
    }
    
    @PostMapping("/generate-all")
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

    @PostMapping("/customer/{customerId}")
    public Bill createBillForCustomer(
            @PathVariable Long customerId,
            @RequestBody List<ProductSelection> selections // Produits + quantités choisis
    ) {
        Customer customer = customerRestClient.findCustomerById(customerId);
        if (customer == null) {
            return null;
        }

        Bill bill = Bill.builder()
                .billingDate(new Date())
                .customerId(customerId)
                .build();
        billRepository.save(bill);

        int totalItems = 0;
        double totalAmount = 0.0;

        // Pour chaque produit choisi
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

    /* ========================================
     * ANCIEN CODE (AVANT KAFKA) - COMMENTÉ
     * ========================================
     * 
     * Ce code fonctionnait mais avait des limitations:
     * 
     * @PostMapping("/customer/{customerId}")
     * public Bill createBillForCustomer(@PathVariable Long customerId) {
     *     // 1. Récupération du client (SYNCHRONE - OK)
     *     Customer customer = customerRestClient.findCustomerById(customerId);
     *     if (customer == null) {
     *         return null;
     *     }
     *     
     *     // 2. Création de la facture (OK)
     *     Bill bill = Bill.builder()
     *             .billingDate(new Date())
     *             .customerId(customerId)
     *             .build();
     *     billRepository.save(bill);
     *     
     *     // 3. Création des items (OK)
     *     List<Product> products = productRestClient.getAllProducts();
     *     products.forEach(product -> {
     *         ProductItem productItem = ProductItem.builder()
     *                 .bill(bill)
     *                 .productId(product.getId())
     *                 .quantity(1 + (int)(Math.random() * 10))
     *                 .unitPrice(product.getPrice())
     *                 .build();
     *         productItemRepository.save(productItem);
     *     });
     *     
     *     // 4. PROBLÈME: Comment notifier d'autres services?
     *     // Option 1: Appel REST direct (COUPLAGE FORT, LENT)
     *     // notificationService.sendEmail(customer.getEmail()); ❌
     *     // statisticsService.updateStats(); ❌
     *     // 
     *     // Option 2: Kafka (COUPLAGE FAIBLE, RAPIDE) ✅
     *     // billEventProducer.sendBillCreatedEvent(event);
     *     
     *     // 5. Retour de la facture
     *     bill.setCustomer(customer);
     *     return bill;
     * }
     * 
     * LIMITATIONS DE L'ANCIEN CODE:
     * 1. Couplage fort: Si on veut ajouter une notification, il faut modifier ce code
     * 2. Performance: Si on appelle plusieurs services, le temps de réponse augmente
     * 3. Résilience: Si un service est down, toute l'opération échoue
     * 4. Scalabilité: Difficile d'ajouter de nouveaux traitements post-création
     * 
     * AVANTAGES AVEC KAFKA:
     * 1. Découplage: On publie un événement, les consommateurs s'abonnent
     * 2. Performance: Publication asynchrone = réponse HTTP rapide
     * 3. Résilience: Si un consommateur est down, les autres continuent
     * 4. Scalabilité: Facile d'ajouter de nouveaux consommateurs sans modifier ce code
     */


}

