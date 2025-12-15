package com.livecoding.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * ========================================
 * BILL CREATED EVENT - ÉVÉNEMENT KAFKA
 * ========================================
 * 
 * Cette classe représente un événement publié sur Kafka lorsqu'une facture est créée.
 * 
 * CONCEPT: Event-Driven Architecture (Architecture pilotée par les événements)
 * - Au lieu d'appeler directement un autre service (couplage fort),
 * - On publie un événement et d'autres services peuvent y réagir (couplage faible)
 * 
 * AVANTAGES:
 * 1. Découplage: Le billing-service ne connaît pas les consommateurs
 * 2. Scalabilité: Plusieurs services peuvent consommer le même événement
 * 3. Résilience: Si un consommateur est down, le message est conservé
 * 4. Asynchrone: Le producteur continue sans attendre la réponse
 * 
 * EXEMPLE D'UTILISATION:
 * - Billing Service publie "BillCreated"
 * - Notification Service envoie un email au client
 * - Analytics Service met à jour les statistiques
 * - Accounting Service enregistre en comptabilité
 * 
 * NOTE: Implements Serializable pour que Kafka puisse sérialiser l'objet en bytes
 */
@Data                   // Génère getters, setters, toString, equals, hashCode
@NoArgsConstructor       // Constructeur sans arguments (requis pour la désérialisation JSON)
@AllArgsConstructor      // Constructeur avec tous les arguments
@Builder                 // Pattern Builder pour créer facilement des instances
public class BillCreatedEvent implements Serializable {
    
    /**
     * ID de la facture créée
     * Utilisé comme clé du message Kafka pour garantir l'ordre des événements
     * pour une même facture
     */
    private Long billId;
    
    /**
     * ID du client pour lequel la facture a été créée
     * Permet aux consommateurs de récupérer plus d'infos si nécessaire
     */
    private Long customerId;
    
    /**
     * Nom du client
     * Données dénormalisées pour éviter aux consommateurs de faire des appels REST
     * PRINCIPE: L'événement contient toutes les données nécessaires (self-contained event)
     */
    private String customerName;
    
    /**
     * Email du client
     * Permet au service de notification d'envoyer un email sans appeler customer-service
     */
    private String customerEmail;
    
    /**
     * Date de création de la facture
     */
    private Date billingDate;
    
    /**
     * Nombre total d'items dans la facture
     * Information agrégée pour les statistiques
     */
    private int totalItems;
    
    /**
     * Montant total de la facture
     * Permet aux services de calculer des statistiques sans recalculer
     */
    private double totalAmount;
    
    // ========================================
    // NOTES SUR LA SÉRIALISATION KAFKA
    // ========================================
    /*
     * Kafka stocke les messages sous forme de bytes.
     * La sérialisation transforme l'objet Java en JSON, puis en bytes:
     * 
     * BillCreatedEvent (Java Object)
     *         ↓ (JsonSerializer)
     * {"billId": 1, "customerName": "John"} (JSON String)
     *         ↓ (StringSerializer)
     * [01101000 01100101 01101100 01101100 01101111] (Bytes)
     * 
     * Le consommateur fait l'inverse:
     * Bytes → JSON → BillCreatedEvent
     */
}
