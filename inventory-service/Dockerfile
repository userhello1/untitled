# ========================================
# DOCKERFILE - INVENTORY SERVICE
# ========================================
# Ce service gère les produits/inventaire (CRUD operations)
# Il s'enregistre automatiquement dans Eureka
# Utilise H2 comme base de données en mémoire

# ========================================
# ÉTAPE 1: BUILD
# ========================================
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

# Télécharger les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compiler l'application
COPY src ./src
RUN mvn clean package -DskipTests

# ========================================
# ÉTAPE 2: RUNTIME
# ========================================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copier le JAR
COPY --from=build /app/target/*.jar app.jar

# Exposer le port 8082 (port de l'inventory-service)
EXPOSE 8082

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
