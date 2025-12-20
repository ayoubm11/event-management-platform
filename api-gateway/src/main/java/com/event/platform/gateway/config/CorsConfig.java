package com.event.platform.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Configuration CORS pour le Gateway
 *
 * CORS (Cross-Origin Resource Sharing) permet au frontend Angular
 * (sur http://localhost:4200) de faire des requêtes vers le backend
 * (sur http://localhost:8080)
 *
 * Sans cette configuration, le navigateur bloquerait les requêtes
 * pour des raisons de sécurité.
 */
@Configuration
public class CorsConfig {

    /**
     * Configure le filtre CORS global
     *
     * @return CorsWebFilter configuré
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Origines autorisées (frontend Angular)
        // En production, remplacez par votre domaine réel
        corsConfig.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",
                "http://localhost:4201"
        ));

        // Méthodes HTTP autorisées
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Headers autorisés dans les requêtes
        corsConfig.setAllowedHeaders(Collections.singletonList("*"));

        // Permet l'envoi de credentials (cookies, auth headers)
        corsConfig.setAllowCredentials(true);

        // Headers exposés au client
        corsConfig.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type"
        ));

        // Durée de cache de la preflight request (en secondes)
        corsConfig.setMaxAge(3600L);

        // Applique la config CORS à tous les paths
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}