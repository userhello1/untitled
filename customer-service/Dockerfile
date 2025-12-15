# ========================================
# STAGE 1: BUILD COMMON-EVENTS
# ========================================
FROM maven:3.8.5-openjdk-17 AS common-events-build

WORKDIR /build/common-events

# Chemins relatifs AU CONTEXTE de docker-compose = untitled/
COPY common-events/pom.xml ./pom.xml
COPY common-events/src ./src

RUN mvn -q clean install


# ========================================
# STAGE 2: BUILD CUSTOMER-SERVICE
# ========================================
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

# Copier le customer-service
COPY customer-service/pom.xml ./pom.xml
COPY customer-service/src ./src

# Copier le .m2 contenant common-events
COPY --from=common-events-build /root/.m2 /root/.m2

RUN mvn -q clean package -DskipTests


# ========================================
# STAGE 3: RUNTIME
# ========================================
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
