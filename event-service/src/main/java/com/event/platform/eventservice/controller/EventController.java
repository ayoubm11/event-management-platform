package com.event.platform.eventservice.controller;

import com.event.platform.eventservice.dto.EventDTO;
import com.event.platform.eventservice.model.EventCategory;
import com.event.platform.eventservice.service.EventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des événements
 *
 * Ce contrôleur expose les endpoints API pour:
 * - CRUD des événements
 * - Recherche et filtrage
 * - Gestion du statut
 * - Réservation de places
 *
 * Base path: /events
 *
 * Annotations:
 * - @RestController: Combine @Controller et @ResponseBody
 * - @RequestMapping: Définit le chemin de base
 * - @CrossOrigin: Autorise les requêtes depuis le frontend
 */
@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "http://localhost:4200")
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Crée un nouvel événement
     * POST /events
     *
     * @param eventDTO Données de l'événement à créer
     * @return L'événement créé avec status 201
     */
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(
            @Valid @RequestBody EventDTO eventDTO) {
        log.info("POST /events - Création d'un événement: {}", eventDTO.getName());

        EventDTO createdEvent = eventService.createEvent(eventDTO);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    /**
     * Récupère tous les événements
     * GET /events
     *
     * @return Liste de tous les événements
     */
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        log.info("GET /events - Récupération de tous les événements");

        List<EventDTO> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    /**
     * Récupère un événement par son ID
     * GET /events/{id}
     *
     * @param id ID de l'événement
     * @return L'événement trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        log.info("GET /events/{} - Récupération de l'événement", id);

        EventDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    /**
     * Récupère les événements disponibles
     * GET /events/available
     *
     * @return Liste des événements disponibles
     */
    @GetMapping("/available")
    public ResponseEntity<List<EventDTO>> getAvailableEvents() {
        log.info("GET /events/available - Événements disponibles");

        List<EventDTO> events = eventService.getAvailableEvents();
        return ResponseEntity.ok(events);
    }

    /**
     * Recherche des événements par mot-clé
     * GET /events/search?keyword=concert
     *
     * @param keyword Mot-clé de recherche
     * @return Liste des événements correspondants
     */
    @GetMapping("/search")
    public ResponseEntity<List<EventDTO>> searchEvents(
            @RequestParam String keyword) {
        log.info("GET /events/search?keyword={}", keyword);

        List<EventDTO> events = eventService.searchEvents(keyword);
        return ResponseEntity.ok(events);
    }

    /**
     * Filtre les événements par catégorie
     * GET /events/category/SPORT
     *
     * @param category Catégorie d'événement
     * @return Liste des événements de cette catégorie
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<EventDTO>> getEventsByCategory(
            @PathVariable EventCategory category) {
        log.info("GET /events/category/{}", category);

        List<EventDTO> events = eventService.getEventsByCategory(category);
        return ResponseEntity.ok(events);
    }

    /**
     * Met à jour un événement
     * PUT /events/{id}
     *
     * @param id ID de l'événement à modifier
     * @param eventDTO Nouvelles données
     * @return L'événement mis à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventDTO eventDTO) {
        log.info("PUT /events/{} - Mise à jour de l'événement", id);

        EventDTO updatedEvent = eventService.updateEvent(id, eventDTO);
        return ResponseEntity.ok(updatedEvent);
    }

    /**
     * Publie un événement
     * PATCH /events/{id}/publish
     *
     * @param id ID de l'événement
     * @return L'événement publié
     */
    @PatchMapping("/{id}/publish")
    public ResponseEntity<EventDTO> publishEvent(@PathVariable Long id) {
        log.info("PATCH /events/{}/publish", id);

        EventDTO publishedEvent = eventService.publishEvent(id);
        return ResponseEntity.ok(publishedEvent);
    }

    /**
     * Annule un événement
     * PATCH /events/{id}/cancel
     *
     * @param id ID de l'événement
     * @return L'événement annulé
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<EventDTO> cancelEvent(@PathVariable Long id) {
        log.info("PATCH /events/{}/cancel", id);

        EventDTO cancelledEvent = eventService.cancelEvent(id);
        return ResponseEntity.ok(cancelledEvent);
    }

    /**
     * Réserve des places pour un événement
     * POST /events/{id}/reserve
     *
     * Cette méthode est appelée par le Booking Service
     *
     * @param id ID de l'événement
     * @param numberOfSeats Nombre de places à réserver
     * @return true si la réservation est réussie
     */
    @PostMapping("/{id}/reserve")
    public ResponseEntity<Boolean> reserveSeats(
            @PathVariable Long id,
            @RequestParam int numberOfSeats) {
        log.info("POST /events/{}/reserve - {} places", id, numberOfSeats);

        boolean reserved = eventService.reserveSeats(id, numberOfSeats);
        return ResponseEntity.ok(reserved);
    }

    /**
     * Libère des places (annulation de réservation)
     * POST /events/{id}/release
     *
     * @param id ID de l'événement
     * @param numberOfSeats Nombre de places à libérer
     * @return 200 OK
     */
    @PostMapping("/{id}/release")
    public ResponseEntity<Void> releaseSeats(
            @PathVariable Long id,
            @RequestParam int numberOfSeats) {
        log.info("POST /events/{}/release - {} places", id, numberOfSeats);

        eventService.releaseSeats(id, numberOfSeats);
        return ResponseEntity.ok().build();
    }

    /**
     * Supprime un événement
     * DELETE /events/{id}
     *
     * @param id ID de l'événement à supprimer
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        log.info("DELETE /events/{}", id);

        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Health check endpoint
     * GET /events/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Event Service is running");
    }
}