# ========================================
# STAGE 1: BUILD COMMON-EVENTS
# ========================================
FROM maven:3.8.5-openjdk-17 AS common-events-build

WORKDIR /build/common-events

# Copier le module commun
COPY common-events/pom.xml ./pom.xml
COPY common-events/src ./src

# Installer common-events dans le repo local du container
RUN mvn -q clean install


# ========================================
# STAGE 2: BUILD BILLING SERVICE
# ========================================
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

# Copier pom + code billing-service
COPY billing-service/pom.xml .
COPY billing-service/src ./src

# Copier le .m2 contenant common-events compilé
COPY --from=common-events-build /root/.m2 /root/.m2

# Build de billing-service
RUN mvn -q clean package -DskipTests


# ========================================
# STAGE 3: RUNTIME
# ========================================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copier le JAR final
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
