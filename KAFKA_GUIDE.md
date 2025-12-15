# 🚀 GUIDE KAFKA - COMMUNICATION ASYNCHRONE

## 📚 RÉSUMÉ DES CONCEPTS

### **Qu'est-ce que Kafka ?**
Kafka est un système de **messagerie distribué** qui permet aux microservices de communiquer de manière **asynchrone** via des **événements**.

### **Différence SYNCHRONE vs ASYNCHRONE**

#### SYNCHRONE (REST/Feign) :
```
Service A → REST → Service B → Attend la réponse → Continue
           |------ Bloqué ici ------|
```
- ✅ Simple à comprendre
- ✅ Réponse immédiate
- ❌ Couplage fort (Service A dépend de Service B)
- ❌ Lent si plusieurs appels
- ❌ Si Service B est down, Service A échoue

#### ASYNCHRONE (Kafka) :
```
Service A → Kafka → Continue immédiatement
               ↓
         Service B (écoute et traite en arrière-plan)
         Service C (écoute et traite en arrière-plan)
         Service D (écoute et traite en arrière-plan)
```
- ✅ Découplage (Service A ne connaît pas B, C, D)
- ✅ Rapide (pas d'attente)
- ✅ Résilient (si B est down, A continue)
- ✅ Scalable (facile d'ajouter Service E, F, G...)
- ❌ Plus complexe à mettre en place

---

## 🏗️ ARCHITECTURE MISE EN PLACE

### **Composants créés :**

1. **`docker-compose.yml`** → Infrastructure Kafka
   - Zookeeper (coordination)
   - Kafka Broker (serveur de messages)

2. **`BillCreatedEvent`** → L'événement publié
   - billId, customerId, customerName, customerEmail
   - billingDate, totalItems, totalAmount

3. **`application.properties`** → Configuration Kafka
   - Producer config (sérialiseurs, bootstrap servers)
   - Consumer config (désérialiseurs, group ID)

4. **`BillEventProducer`** → Publie les événements
   - Utilise KafkaTemplate
   - Envoie sur le topic "bill-created-topic"

5. **`BillEventConsumer`** → Écoute les événements
   - @KafkaListener sur "bill-created-topic"
   - Traite les événements (log, email, stats...)

6. **`BillingRestController`** → Modifié
   - Crée la facture (SYNCHRONE)
   - Publie l'événement (ASYNCHRONE)
   - Retourne la réponse HTTP

### **Flux complet :**

```
1. POST /api/bills/customer/1
              ↓
2. BillingRestController
   - Récupère customer via Feign (SYNCHRONE)
   - Crée Bill + ProductItems en BDD
   - Construit BillCreatedEvent
              ↓
3. BillEventProducer.sendBillCreatedEvent()
   - Sérialise en JSON
   - Envoie à Kafka
              ↓
4. Kafka Broker
   - Stocke le message dans "bill-created-topic"
   - Distribue aux consommateurs
              ↓
5. BillEventConsumer.consumeBillCreatedEvent()
   - Désérialise le JSON
   - Traite l'événement (log, email, etc.)
              ↓
6. Autres services potentiels
   - NotificationService (envoie email)
   - AnalyticsService (met à jour stats)
   - AccountingService (enregistre en compta)
```

---

## 🧪 COMMENT TESTER

### **Étape 1 : Démarrer Kafka**

```powershell
# Dans le répertoire racine (où se trouve docker-compose.yml)
docker-compose up -d

# Vérifier que Kafka est démarré
docker-compose ps

# Voir les logs
docker-compose logs -f kafka
```

**Ports utilisés :**
- Zookeeper : `2181`
- Kafka : `9092`

---

### **Étape 2 : Démarrer les microservices**

Dans cet ordre :

1. **Discovery Service** (port 8761)
   ```powershell
   cd discovery-service
   mvn spring-boot:run
   ```

2. **Customer Service** (port 8081)
   ```powershell
   cd customer-service
   mvn spring-boot:run
   ```

3. **Inventory Service** (port 8082)
   ```powershell
   cd inventoryService
   mvn spring-boot:run
   ```

4. **Billing Service** (port 8083)
   ```powershell
   cd billing-service
   mvn spring-boot:run
   ```

5. **Gateway** (port 8888) - Optionnel
   ```powershell
   cd gateway
   mvn spring-boot:run
   ```

---

### **Étape 3 : Créer une facture et observer Kafka**

#### **Créer une facture via REST :**

```http
POST http://localhost:8083/api/bills/customer/1
```

Ou via PowerShell :
```powershell
Invoke-RestMethod -Uri "http://localhost:8083/api/bills/customer/1" -Method Post
```

#### **Observer les logs du billing-service :**

Vous devriez voir :

```
========================================
📤 KAFKA PRODUCER: Publication d'un événement
   → Topic: bill-created-topic
   → Key (billId): 1
   → Customer: John Doe (john@example.com)
   → Date: Sat Nov 23 2025 10:30:00
   → Total items: 25
   → Total amount: 1250.5 €
========================================
✅ Événement envoyé avec succès à Kafka !
========================================

[Quelques millisecondes après...]

========================================
📥 KAFKA CONSUMER: Événement reçu !
   → Topic: bill-created-topic
   → Consumer Group: billing-group
========================================
📄 DÉTAILS DE LA FACTURE:
   → Bill ID: 1
   → Customer ID: 1
   → Customer Name: John Doe
   → Customer Email: john@example.com
   → Billing Date: Sat Nov 23 2025 10:30:00
   → Total Items: 25
   → Total Amount: 1250.5 €
========================================
📧 Envoi d'un email de confirmation à john@example.com
📊 Mise à jour des statistiques de facturation
🔔 Envoi d'une notification push au client
📦 Archivage de la facture pour la comptabilité
📝 Enregistrement dans le journal d'audit
✅ Événement traité avec succès !
========================================
```

---

### **Étape 4 : Vérifier Kafka (Optionnel)**

#### **Lister les topics :**
```powershell
docker exec -it kafka-broker kafka-topics --list --bootstrap-server localhost:9092
```

Vous devriez voir : `bill-created-topic`

#### **Lire les messages du topic :**
```powershell
docker exec -it kafka-broker kafka-console-consumer --topic bill-created-topic --from-beginning --bootstrap-server localhost:9092
```

Vous verrez les événements au format JSON :
```json
{
  "billId": 1,
  "customerId": 1,
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "billingDate": 1700736600000,
  "totalItems": 25,
  "totalAmount": 1250.5
}
```

---

## 🔄 TESTER AVEC PLUSIEURS FACTURES

```powershell
# Créer plusieurs factures
Invoke-RestMethod -Uri "http://localhost:8083/api/bills/customer/1" -Method Post
Invoke-RestMethod -Uri "http://localhost:8083/api/bills/customer/2" -Method Post
Invoke-RestMethod -Uri "http://localhost:8083/api/bills/customer/3" -Method Post
```

Observez les logs : vous verrez 3 événements publiés et consommés !

---

## 📊 VÉRIFIER LES FACTURES EN BASE

```http
GET http://localhost:8083/api/bills
```

Ou :
```powershell
Invoke-RestMethod -Uri "http://localhost:8083/api/bills"
```

---

## 🛠️ CONCEPTS AVANCÉS

### **Consumer Groups**

Dans `application.properties`, on a défini `spring.kafka.consumer.group-id=billing-group`.

**Qu'est-ce qu'un Consumer Group ?**
- Les consommateurs du **même groupe** se **partagent** les messages (load balancing)
- Les consommateurs de **groupes différents** reçoivent **tous** les messages (broadcast)

**Exemple :**

```
Topic: bill-created-topic (3 partitions)

Consumer Group A (billing-group):
  - Consumer A1 lit: Partition 0, 1
  - Consumer A2 lit: Partition 2

Consumer Group B (notification-group):
  - Consumer B1 lit: Partition 0, 1, 2

Résultat:
- Dans le groupe A, chaque message va à 1 seul consumer (load balancing)
- Le groupe B reçoit AUSSI tous les messages (broadcast)
```

**Cas d'usage :**
- `billing-group` : Traite la facture (1 seul consumer doit le faire)
- `notification-group` : Envoie email (séparé, peut échouer indépendamment)
- `analytics-group` : Met à jour stats (séparé, peut échouer indépendamment)

---

### **Partitions et Ordre des Messages**

Dans `docker-compose.yml`, on a configuré 3 partitions par défaut.

**À quoi servent les partitions ?**
1. **Parallélisme** : Plusieurs consumers peuvent lire en parallèle
2. **Ordre garanti** : Messages avec la même clé vont dans la même partition

**Exemple :**

```
Topic: bill-created-topic (3 partitions)

Message 1 (billId=1) → hash(1) % 3 = Partition 1
Message 2 (billId=2) → hash(2) % 3 = Partition 2
Message 3 (billId=3) → hash(3) % 3 = Partition 0
Message 4 (billId=1) → hash(1) % 3 = Partition 1 (même partition que Message 1 !)

Résultat:
- Tous les événements de la facture #1 sont dans la Partition 1
- L'ordre est garanti pour la facture #1
- Les factures différentes peuvent être traitées en parallèle
```

---

### **Rétention des Messages**

Dans `docker-compose.yml`, on a configuré `KAFKA_LOG_RETENTION_MS: 604800000` (7 jours).

**Que se passe-t-il après 7 jours ?**
- Les anciens messages sont automatiquement supprimés
- Les nouveaux consumers ne voient pas les messages de plus de 7 jours
- En production, ajustez selon vos besoins (1 jour, 30 jours, infini...)

---

### **Offsets**

**C'est quoi un offset ?**
- Position de lecture dans une partition (comme un marque-page)
- Kafka se souvient où chaque consumer group a lu

**Exemple :**

```
Partition 0: [Msg0, Msg1, Msg2, Msg3, Msg4, Msg5]
                                    ↑
                        billing-group offset = 3
                        (a lu jusqu'à Msg3, prochain = Msg4)
```

**Si le consumer redémarre :**
- Il reprend à l'offset 3 (Msg4)
- Il ne relit pas les anciens messages déjà traités
- C'est la magie de `auto-commit` !

**Auto-offset-reset:**
- `earliest` : Si aucun offset, lit depuis le début
- `latest` : Si aucun offset, lit uniquement les nouveaux
- `none` : Si aucun offset, erreur

---

## 🎯 EXERCICES PRATIQUES

### **Exercice 1 : Ajouter un champ à l'événement**

1. Ajoutez un champ `status` dans `BillCreatedEvent`
2. Modifiez le `BillingRestController` pour le remplir
3. Modifiez le `BillEventConsumer` pour l'afficher

### **Exercice 2 : Créer un nouveau Consumer**

1. Créez un nouveau service (ex: `notification-service`)
2. Ajoutez la dépendance Kafka
3. Créez un `@KafkaListener` avec un `groupId` différent
4. Testez : les 2 consumers doivent recevoir le même événement !

### **Exercice 3 : Filtrer les événements**

Dans `BillEventConsumer`, traitez uniquement les factures > 500€ :

```java
@KafkaListener(topics = "bill-created-topic", groupId = "billing-group")
public void consumeBillCreatedEvent(BillCreatedEvent event) {
    if (event.getTotalAmount() < 500) {
        log.info("⏭️ Facture ignorée (montant trop faible)");
        return;
    }
    // Traiter uniquement les grandes factures
}
```

### **Exercice 4 : Créer un nouvel événement**

Créez un événement `BillPaidEvent` qui est publié quand une facture est payée :

1. Créer `BillPaidEvent.java`
2. Créer un endpoint `POST /api/bills/{id}/pay`
3. Publier l'événement dans le endpoint
4. Créer un consumer qui réagit à cet événement

---

## 🧹 NETTOYAGE

### **Arrêter Kafka :**
```powershell
docker-compose down
```

### **Supprimer les données Kafka :**
```powershell
docker-compose down -v
```

---

## 🎓 CONCEPTS CLÉ À RETENIR

1. **Kafka = Communication asynchrone** entre microservices
2. **Producer** = Publie des événements
3. **Consumer** = Écoute et traite des événements
4. **Topic** = Canal de communication
5. **Partition** = Division d'un topic (parallélisme + ordre)
6. **Consumer Group** = Groupe de consumers (load balancing)
7. **Offset** = Position de lecture (comme un marque-page)
8. **Asynchrone = Rapide + Découplé + Résilient**

---

## 📖 POUR ALLER PLUS LOIN

- **Kafka Streams** : Traitement de flux en temps réel
- **Kafka Connect** : Intégration avec bases de données
- **Schema Registry** : Gestion des schémas Avro/Protobuf
- **KSQL** : SQL pour interroger les streams Kafka
- **Dead Letter Topics** : Gestion des messages en erreur
- **Idempotence** : Éviter les doublons de messages
- **Exactly-once semantics** : Garantie de traitement unique

---

## 🆘 DÉPANNAGE

### **Erreur: Connection refused (Kafka)**
→ Vérifiez que Docker est démarré : `docker ps`

### **Erreur: Topic does not exist**
→ Kafka crée automatiquement les topics, attendez quelques secondes

### **Le consumer ne reçoit pas les messages**
→ Vérifiez le `group-id` et `auto-offset-reset` dans application.properties

### **Les messages sont dupliqués**
→ Désactivez `auto-commit` et commitez manuellement

### **Kafka est lent**
→ Augmentez le nombre de partitions
→ Ajoutez plus de consumers (1 par partition max)

---

**🎉 Félicitations ! Vous avez implémenté Kafka avec succès ! 🎉**
