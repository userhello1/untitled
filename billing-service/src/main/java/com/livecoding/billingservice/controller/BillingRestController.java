package com.livecoding.billingservice.controller;

import com.livecoding.billingservice.dto.ProductSelection;
import com.livecoding.billingservice.service.BillingService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/bills")
public class BillingRestController {

    private final BillingService billingService;

    @Autowired
    public BillingRestController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {
        return ResponseEntity.ok(billingService.getAllBills());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBillById(@PathVariable Long id) {
        Bill bill = billingService.getBillById(id);
        if (bill == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(bill);
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<ProductItem>> getBillItems(@PathVariable Long id) {
        List<ProductItem> items = billingService.getBillItems(id);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/generate-all")
    public ResponseEntity<String> generateAllBills() {
        String result = billingService.generateAllBills();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/customer/{customerId}")
    public ResponseEntity<Bill> createBillForCustomer(
            @PathVariable Long customerId,
            @RequestBody List<ProductSelection> selections
    ) {
        try {
            Bill bill = billingService.createBillForCustomer(customerId, selections);
            return ResponseEntity.ok(bill);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
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

