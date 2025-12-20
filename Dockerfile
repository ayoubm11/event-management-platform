# Dockerfile pour le projet parent TKEV
# Ce Dockerfile n'est utilisé que si vous voulez build tout le projet en une fois
# Pour les microservices individuels, utilisez les Dockerfiles dans chaque service

# ========== STAGE 1: BUILD ALL SERVICES ==========
FROM maven:3.9-eclipse-temurin-17 AS build

# Définit le répertoire de travail
WORKDIR /app

# Copie le POM parent et tous les modules
COPY pom.xml .
COPY config-server ./config-server
COPY eureka-server ./eureka-server
COPY api-gateway ./api-gateway
COPY event-service ./event-service
COPY booking-service ./booking-service

# Build tous les services
RUN mvn clean package -DskipTests

# Note: Ce Dockerfile build TOUS les services
# En production, utilisez Docker Compose avec les Dockerfiles individuels
# pour un meilleur contrôle et des builds plus rapides

# ========== STAGE 2: RUNTIME (OPTIONNEL) ==========
# Ce stage n'est pas utilisé car chaque service a son propre Dockerfile
# Gardez ce fichier pour documentation ou build en une fois si nécessaire

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN echo "Ce Dockerfile est pour le build complet du projet."
RUN echo "Utilisez docker-compose pour lancer les services individuellement."
CMD ["echo", "Utilisez docker-compose up pour démarrer tous les services"]