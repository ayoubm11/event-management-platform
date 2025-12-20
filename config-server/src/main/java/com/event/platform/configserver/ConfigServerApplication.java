package com.event.platform.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Application principale du Config Server
 *
 * Ce serveur centralise toutes les configurations des microservices.
 * Les configurations sont stockées dans un repository Git et sont
 * accessibles par tous les microservices au démarrage.
 *
 * Avantages:
 * - Modification des configs sans redéploiement
 * - Versionnement des configurations via Git
 * - Configuration centralisée et cohérente
 */
@SpringBootApplication
@EnableConfigServer  // Active le serveur de configuration Spring Cloud
public class ConfigServerApplication {

    /**
     * Point d'entrée de l'application
     * @param args Arguments de ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}