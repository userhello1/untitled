# ========================================
# DOCKERFILE - GATEWAY SERVICE
# ========================================
# Ce service est la passerelle API (API Gateway)
# Il joue le rôle de :
# 1. Point d'entrée unique pour tous les microservices
# 2. Routage des requêtes vers les bons services
# 3. Load balancing automatique via Eureka
# 4. Filtrage et sécurité centralisée

# ========================================
# ÉTAPE 1: BUILD
# ========================================
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

# Copier pom.xml et télécharger les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source et compiler
COPY src ./src
RUN mvn clean package -DskipTests

# ========================================
# ÉTAPE 2: RUNTIME
# ========================================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copier le JAR compilé
COPY --from=build /app/target/*.jar app.jar

# Exposer le port 8888 (port du gateway)
EXPOSE 8888

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
