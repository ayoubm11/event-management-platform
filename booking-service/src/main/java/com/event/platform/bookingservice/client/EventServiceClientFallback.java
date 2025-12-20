package com.event.platform.bookingservice.client;

import com.event.platform.bookingservice.dto.EventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback pour EventServiceClient
 *
 * Cette classe implémente le pattern Circuit Breaker.
 * Elle est appelée quand:
 * - Event Service est indisponible
 * - Timeout de la requête
 * - Erreur 5xx du serveur
 *
 * Le fallback permet de:
 * 1. Éviter la propagation des erreurs en cascade
 * 2. Fournir une réponse dégradée mais fonctionnelle
 * 3. Améliorer la résilience du système
 *
 * En production, on pourrait:
 * - Retourner des données en cache
 * - Proposer une alternative
 * - Logger l'incident pour investigation
 */
@Component
@Slf4j
public class EventServiceClientFallback implements EventServiceClient {

    /**
     * Fallback pour getEventById
     *
     * Retourne null car on ne peut pas inventer un événement.
     * Le service appelant doit gérer ce cas.
     */
    @Override
    public EventDTO getEventById(Long id) {
        log.error("FALLBACK: Impossible de récupérer l'événement ID={}. " +
                "Event Service indisponible.", id);

        // En production, on pourrait:
        // 1. Chercher dans un cache Redis
        // 2. Retourner des données partielles
        // 3. Déclencher une alerte

        return null;
    }

    /**
     * Fallback pour reserveSeats
     *
     * Retourne false pour indiquer l'échec.
     * IMPORTANT: Ne pas confirmer une réservation si on ne peut pas
     * communiquer avec Event Service!
     */
    @Override
    public Boolean reserveSeats(Long eventId, int numberOfSeats) {
        log.error("FALLBACK: Impossible de réserver {} places pour l'événement ID={}. " +
                "Event Service indisponible.", numberOfSeats, eventId);

        // Retourne false pour empêcher la création de la réservation
        // sans avoir réservé les places
        return false;
    }

    /**
     * Fallback pour releaseSeats
     *
     * Log l'erreur mais ne bloque pas l'annulation.
     * Les places seront libérées lors du prochain retry ou manuellement.
     */
    @Override
    public void releaseSeats(Long eventId, int numberOfSeats) {
        log.error("FALLBACK: Impossible de libérer {} places pour l'événement ID={}. " +
                        "Event Service indisponible. Tentative ultérieure nécessaire.",
                numberOfSeats, eventId);

        // En production:
        // 1. Enregistrer dans une queue de retry
        // 2. Déclencher une compensation manuelle
        // 3. Alerter l'équipe ops
    }
}