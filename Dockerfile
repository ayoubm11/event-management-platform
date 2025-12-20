# Dockerfile multi-stage pour microservices Spring Boot
# Ce fichier crée une image Docker optimisée en deux étapes

# ========== STAGE 1: BUILD ==========
# Utilise Maven pour compiler l'application
FROM maven:3.9-eclipse-temurin-17 AS build

# Définit le répertoire de travail
WORKDIR /app

# Copie les fichiers de configuration Maven
# (Copier d'abord les fichiers de config permet de cacher cette couche)
COPY pom.xml .
COPY src ./src

# Compile l'application et crée le JAR
# -DskipTests: ignore les tests pour accélérer le build
# clean: nettoie les builds précédents
# package: crée le JAR
RUN mvn clean package -DskipTests

# ========== STAGE 2: RUNTIME ==========
# Image légère pour exécuter l'application
FROM eclipse-temurin:17-jre-alpine

# Métadonnées de l'image
LABEL maintainer="votre-email@example.com"
LABEL description="Microservice Spring Boot pour la plateforme d'événements"

# Crée un utilisateur non-root pour la sécurité
# (Ne jamais exécuter une app en tant que root dans un container)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Définit le répertoire de travail
WORKDIR /app

# Copie le JAR depuis le stage de build
# --from=build: récupère depuis le stage précédent
COPY --from=build /app/target/*.jar app.jar

# Expose le port de l'application
# (Informatif uniquement, ne publie pas réellement le port)
EXPOSE 8080

# Définit les variables d'environnement JVM
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Point d'entrée de l'application
# exec java: permet de recevoir les signaux (SIGTERM, etc.)
# $JAVA_OPTS: options JVM configurables
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

# Health check
# Vérifie que l'application répond correctement
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1