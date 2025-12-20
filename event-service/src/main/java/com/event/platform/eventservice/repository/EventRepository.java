package com.event.platform.eventservice.repository;

import com.event.platform.eventservice.model.Event;
import com.event.platform.eventservice.model.EventCategory;
import com.event.platform.eventservice.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Event
 *
 * Spring Data JPA génère automatiquement l'implémentation des méthodes.
 * Cette interface définit les opérations de base de données pour les événements.
 *
 * Hérite de JpaRepository qui fournit:
 * - save(), findById(), findAll(), delete()
 * - Méthodes de pagination et tri
 * - Flush et batch operations
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Trouve tous les événements publiés
     * Query method: Spring génère le SQL automatiquement
     */
    List<Event> findByStatus(EventStatus status);

    /**
     * Trouve les événements par catégorie
     */
    List<Event> findByCategory(EventCategory category);

    /**
     * Trouve les événements d'un organisateur spécifique
     */
    List<Event> findByOrganizerId(Long organizerId);

    /**
     * Trouve les événements dont la date de début est après une date donnée
     */
    List<Event> findByStartDateAfter(LocalDateTime date);

    /**
     * Trouve les événements publiés avec des places disponibles
     * Utilise une query custom pour plus de contrôle
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' " +
            "AND e.availableSeats > 0 " +
            "AND e.startDate > :currentDate " +
            "ORDER BY e.startDate ASC")
    List<Event> findAvailableEvents(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Recherche d'événements par nom ou description
     * LIKE pour recherche partielle (insensible à la casse)
     */
    @Query("SELECT e FROM Event e WHERE " +
            "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Event> searchEvents(@Param("keyword") String keyword);

    /**
     * Trouve les événements par catégorie et statut
     * Combinaison de plusieurs critères
     */
    List<Event> findByCategoryAndStatus(EventCategory category, EventStatus status);

    /**
     * Trouve les événements dans une plage de dates
     */
    @Query("SELECT e FROM Event e WHERE e.startDate BETWEEN :startDate AND :endDate")
    List<Event> findEventsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Compte le nombre d'événements d'un organisateur
     */
    long countByOrganizerId(Long organizerId);

    /**
     * Vérifie si un événement existe et a des places disponibles
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM Event e WHERE e.id = :eventId " +
            "AND e.availableSeats >= :requiredSeats")
    boolean hasAvailableSeats(
            @Param("eventId") Long eventId,
            @Param("requiredSeats") int requiredSeats
    );

    /**
     * Lock optimiste pour éviter les problèmes de concurrence
     * lors de la réservation de places
     */
    @Query("SELECT e FROM Event e WHERE e.id = :eventId")
    Optional<Event> findByIdForUpdate(@Param("eventId") Long eventId);
}