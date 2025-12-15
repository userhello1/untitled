# ========================================
# DOCKERFILE - DISCOVERY SERVICE (EUREKA)
# ========================================
# Ce service est le registre de services (Service Registry)
# Il permet aux microservices de :
# 1. S'enregistrer automatiquement au démarrage
# 2. Se découvrir mutuellement sans connaître leurs adresses IP
# 3. Faire du load balancing automatique

# ========================================
# ÉTAPE 1: BUILD
# ========================================
# Utilise Maven avec OpenJDK 17 pour compiler l'application
FROM maven:3.8.5-openjdk-17 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier pom.xml pour télécharger les dépendances
# (Cache Docker : si pom.xml ne change pas, cette étape est mise en cache)
COPY pom.xml .

# Télécharger les dépendances Maven (mise en cache)
RUN mvn dependency:go-offline -B

# Copier tout le code source
COPY src ./src

# Compiler l'application et créer le JAR
# -DskipTests : ignore les tests pour accélérer le build
RUN mvn clean package -DskipTests

# ========================================
# ÉTAPE 2: RUNTIME
# ========================================
# Utilise une image légère avec seulement le JRE (pas Maven)
FROM eclipse-temurin:17-jre

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR compilé depuis l'étape de build
# target/*.jar = Le JAR généré par Maven
COPY --from=build /app/target/*.jar app.jar

# Exposer le port 8761 (port par défaut d'Eureka)
EXPOSE 8761

# Commande pour lancer l'application
# -Djava.security.egd=file:/dev/./urandom : Améliore le démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]
