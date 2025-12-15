package com.livecoding.billingservice.kafka;

import com.livecoding.events.BillCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * ========================================
 * KAFKA PRODUCER - PRODUCTEUR D'ÉVÉNEMENTS
 * ========================================
 * 
 * Ce service est responsable de PUBLIER des événements sur Kafka.
 * 
 * RÔLE DU PRODUCER:
 * 1. Prendre un événement Java (BillCreatedEvent)
 * 2. Le sérialiser en JSON
 * 3. L'envoyer au broker Kafka sur un TOPIC spécifique
 * 4. Le broker distribue ensuite le message aux consommateurs
 * 
 * FLUX DE DONNÉES:
 * BillingRestController → BillEventProducer → Kafka Broker → BillEventConsumer
 * 
 * CONCEPTS CLÉS:
 * - KafkaTemplate: Classe Spring qui simplifie l'envoi de messages
 * - Topic: Canal de communication (bill-created-topic)
 * - Key: Identifiant du message (billId) pour garantir l'ordre
 * - Value: Le contenu du message (BillCreatedEvent)
 * 
 * ANALOGIE:
 * Le Producer est comme un système de notification push:
 * - Vous créez une notification (événement)
 * - Vous l'envoyez au serveur (Kafka)
 * - Le serveur la distribue à tous les abonnés (consommateurs)
 */
@Service
@Slf4j      // Lombok: Génère automatiquement un logger (log.info, log.error, etc.)
public class BillEventProducer {

    /**
     * KafkaTemplate: API Spring pour envoyer des messages à Kafka
     */
    private final KafkaTemplate<String, BillCreatedEvent> kafkaTemplate;
    

    @Value("${kafka.topic.bill-created}")
    private String topicName;


    public BillEventProducer(KafkaTemplate<String, BillCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBillCreatedEvent(BillCreatedEvent event) {
        // Log avant l'envoi (aide au debugging)
        log.info("========================================");
        log.info("📤 KAFKA PRODUCER: Publication d'un événement");
        log.info("   → Topic: {}", topicName);
        log.info("   → Key (billId): {}", event.getBillId());
        log.info("   → Customer: {} ({})", event.getCustomerName(), event.getCustomerEmail());
        log.info("   → Date: {}", event.getBillingDate());
        log.info("   → Total items: {}", event.getTotalItems());
        log.info("   → Total amount: {} €", event.getTotalAmount());
        log.info("========================================");
        
        kafkaTemplate.send(topicName, event.getBillId().toString(), event);
        
        // Log après l'envoi
        log.info("✅ Événement envoyé avec succès à Kafka !");
        log.info("   Les consommateurs du topic '{}' vont recevoir cet événement", topicName);
        

    }
    
    /*
     * ========================================
     * MÉTHODES AVANCÉES (Optionnel)
     * ========================================
     * 
     * Vous pourriez ajouter d'autres méthodes pour:
     * 
     * 1. Envoi avec callback:
     *    public void sendWithCallback(BillCreatedEvent event) {
     *        kafkaTemplate.send(topicName, event.getBillId().toString(), event)
     *            .addCallback(
     *                success -> log.info("✅ Offset: {}", success.getRecordMetadata().offset()),
     *                failure -> log.error("❌ Erreur: {}", failure.getMessage())
     *            );
     *    }
     * 
     * 2. Envoi synchrone (bloquant):
     *    public void sendSync(BillCreatedEvent event) throws Exception {
     *        kafkaTemplate.send(topicName, event.getBillId().toString(), event).get();
     *        // get() bloque jusqu'à confirmation
     *    }
     * 
     * 3. Envoi en batch (lot):
     *    public void sendBatch(List<BillCreatedEvent> events) {
     *        events.forEach(this::sendBillCreatedEvent);
     *    }
     */
}
