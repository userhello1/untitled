package com.livecoding.customerservice.kafka;

import com.livecoding.events.BillCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service    // Composant Spring géré par le conteneur IoC
@Slf4j      // Lombok: Génère automatiquement un logger
public class BillEventConsumer {

    @KafkaListener(
        topics = "bill-created-topic",      // Topic à écouter (doit correspondre au producer)
        groupId = "billing-group"           // Groupe de consommateurs (load balancing)
    )
    public void consumeBillCreatedEvent(BillCreatedEvent event) {
        /*
         * PROCESSUS DE RECEPTION:
         * 1. Kafka envoie le message (bytes) au consumer
         * 2. Spring deserialise les bytes → JSON → BillCreatedEvent

         */
        
        log.info("========================================");
        log.info("📥 KAFKA CONSUMER: Événement reçu !");
        log.info("   → Topic: bill-created-topic");
        log.info("   → Consumer Group: billing-group");
        log.info("========================================");
        
        // Affichage des détails de l'événement
        log.info("📄 DÉTAILS DE LA FACTURE:");
        log.info("   → Bill ID: {}", event.getBillId());
        log.info("   → Customer ID: {}", event.getCustomerId());
        log.info("   → Customer Name: {}", event.getCustomerName());
        log.info("   → Customer Email: {}", event.getCustomerEmail());
        log.info("   → Billing Date: {}", event.getBillingDate());
        log.info("   → Total Items: {}", event.getTotalItems());
        log.info("   → Total Amount: {} €", event.getTotalAmount());
        log.info("========================================");
        
        /*
         * ========================================
         * LOGIQUE MÉTIER - TRAITEMENT DE L'ÉVÉNEMENT
         * ========================================
         * 
         * C'est ici que vous implémentez la vraie logique:
         */
        
        try {
            // 1. NOTIFICATION PAR EMAIL (exemple)
            log.info("📧 Envoi d'un email de confirmation à {}", event.getCustomerEmail());
            // sendEmail(event.getCustomerEmail(), "Votre facture #" + event.getBillId());
            
            // 2. MISE À JOUR DES STATISTIQUES (exemple)
            log.info("📊 Mise à jour des statistiques de facturation");
            // statisticsService.incrementTotalBills();
            // statisticsService.addRevenue(event.getTotalAmount());
            
            // 3. NOTIFICATION PUSH (exemple)
            log.info("🔔 Envoi d'une notification push au client");
            // notificationService.sendPush(event.getCustomerId(), "Facture créée");
            
            // 4. ARCHIVAGE (exemple)
            log.info("📦 Archivage de la facture pour la comptabilité");
            // archiveService.archive(event);
            
            // 5. AUDIT LOG (exemple)
            log.info("📝 Enregistrement dans le journal d'audit");
            // auditService.log("BILL_CREATED", event.getBillId(), event.getCustomerId());
            
            log.info("✅ Événement traité avec succès !");
            
        } catch (Exception e) {
            /*
             * GESTION D'ERREURS:
             * Si une exception se produit, Kafka peut:
             * 1. Réessayer automatiquement (si configuré)
             * 2. Envoyer le message vers un Dead Letter Topic (DLT)
             * 3. Ignorer le message et continuer
             * 
             * Configuration dans application.properties:
             * spring.kafka.consumer.enable-auto-commit=true
             * = Si erreur, Kafka recommence au dernier message committé
             */
            log.error("❌ Erreur lors du traitement de l'événement: {}", e.getMessage());
            log.error("   → Bill ID: {}", event.getBillId());
            log.error("   → Erreur: ", e);
            
            // Option: Envoyer vers un Dead Letter Topic pour analyse
            // kafkaTemplate.send("bill-created-errors", event);
            
            // Option: Lever une exception pour que Kafka retry
            // throw new RuntimeException("Échec du traitement", e);
        }
        
        log.info("========================================");
    }
    
    /*
     * ========================================
     * MÉTHODES CONSUMER AVANCÉES (Optionnel)
     * ========================================
     * 
     * 1. ÉCOUTER PLUSIEURS TOPICS:
     * @KafkaListener(topics = {"bill-created-topic", "bill-updated-topic"}, groupId = "billing-group")
     * public void consumeMultipleTopics(BillCreatedEvent event) { }
     * 
     * 2. ACCÉDER AUX MÉTADONNÉES DU MESSAGE:
     * @KafkaListener(topics = "bill-created-topic", groupId = "billing-group")
     * public void consumeWithMetadata(
     *     BillCreatedEvent event,
     *     @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
     *     @Header(KafkaHeaders.OFFSET) long offset,
     *     @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp
     * ) {
     *     log.info("Message reçu: partition={}, offset={}, timestamp={}", partition, offset, timestamp);
     * }
     * 
     * 3. FILTRER LES MESSAGES:
     * @KafkaListener(
     *     topics = "bill-created-topic",
     *     groupId = "billing-group",
     *     filter = "billAmountFilter"  // Défini dans une @Bean
     * )
     * public void consumeHighValueBills(BillCreatedEvent event) {
     *     // Ne traite que les factures > 1000€
     * }
     * 
     * 4. TRAITEMENT EN BATCH (LOT):
     * @KafkaListener(topics = "bill-created-topic", groupId = "billing-group")
     * public void consumeBatch(List<BillCreatedEvent> events) {
     *     log.info("Traitement de {} événements en batch", events.size());
     *     events.forEach(this::processEvent);
     * }
     * 
     * 5. COMMIT MANUEL (pour plus de contrôle):
     * @KafkaListener(topics = "bill-created-topic", groupId = "billing-group")
     * public void consumeWithManualCommit(
     *     BillCreatedEvent event,
     *     Acknowledgment acknowledgment
     * ) {
     *     processEvent(event);
     *     acknowledgment.acknowledge(); // Commit manuel
     * }
     */
    
    /*
     * ========================================
     * SCÉNARIOS D'UTILISATION RÉELS
     * ========================================
     * 
     * MICROSERVICE DE NOTIFICATION:
     * - Écoute "bill-created-topic"
     * - Envoie un email au client
     * - Envoie une notification SMS
     * - Envoie une notification push sur l'app mobile
     * 
     * MICROSERVICE D'ANALYTICS:
     * - Écoute "bill-created-topic"
     * - Calcule les KPIs (revenu total, nombre de factures, etc.)
     * - Met à jour les dashboards en temps réel
     * - Génère des rapports automatiques
     * 
     * MICROSERVICE DE COMPTABILITÉ:
     * - Écoute "bill-created-topic"
     * - Enregistre l'écriture comptable
     * - Génère le fichier d'export pour le logiciel comptable
     * - Archive les factures au format PDF
     * 
     * MICROSERVICE DE FRAUD DETECTION:
     * - Écoute "bill-created-topic"
     * - Analyse les patterns suspects
     * - Détecte les anomalies (factures trop élevées, fréquence anormale)
     * - Bloque automatiquement en cas de fraude détectée
     * 
     * AVANTAGE: Tous ces services sont DÉCOUPLÉS
     * Le billing-service ne sait pas qui consomme ses événements
     * On peut ajouter/retirer des consommateurs sans toucher au producteur !
     */
}
