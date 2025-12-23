package com.event.platform.eventservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité Event - Représente un événement dans la base de données
 *
 * Cette classe est mappée à la table 'events' en base de données.
 * Elle contient toutes les informations relatives à un événement.
 *
 * Annotations utilisées:
 * - @Entity: Marque la classe comme entité JPA
 * - @Data: Génère getters, setters, toString, equals, hashCode (Lombok)
 * - @Builder: Permet de construire des objets avec le pattern Builder
 * - @NoArgsConstructor / @AllArgsConstructor: Génère les constructeurs
 */
@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    /**
     * Identifiant unique de l'événement
     * Généré automatiquement par la base de données
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom de l'événement
     * Ne peut pas être null ou vide, max 200 caractères
     */
    @Column(nullable = false, length = 200)
    @NotBlank(message = "Le nom de l'événement est obligatoire")
    @Size(max = 200, message = "Le nom ne peut pas dépasser 200 caractères")
    private String name;

    /**
     * Description détaillée de l'événement
     */
    @Column(columnDefinition = "TEXT")
    @Size(max = 5000, message = "La description ne peut pas dépasser 5000 caractères")
    private String description;

    /**
     * Catégorie de l'événement
     * Valeurs possibles: SPORT, CULTURE, CONFERENCE, CONCERT, etc.
     */
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "La catégorie est obligatoire")
    private EventCategory category;

    /**
     * Lieu où se déroule l'événement
     */
    @Column(nullable = false, length = 300)
    @NotBlank(message = "Le lieu est obligatoire")
    private String location;

    /**
     * Date et heure de début de l'événement
     */
    @Column(nullable = false)
    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être dans le futur")
    private LocalDateTime startDate;

    /**
     * Date et heure de fin de l'événement
     */
    @Column(nullable = false)
    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime endDate;

    /**
     * Capacité maximale de l'événement
     * Nombre total de places disponibles
     */
    @Column(nullable = false)
    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 1, message = "La capacité doit être au moins 1")
    @Max(value = 100000, message = "La capacité ne peut pas dépasser 100000")
    private Integer capacity;

    /**
     * Nombre de places disponibles restantes
     * Initialisé à la capacité, décrémenté à chaque réservation
     */
    @Column(nullable = false)
    private Integer availableSeats;

    /**
     * Prix de base du billet
     * Peut varier selon le type de billet (VIP, Standard, etc.)
     */
    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être positif")
    private BigDecimal basePrice;

    /**
     * Statut de l'événement
     * DRAFT, PUBLISHED, CANCELLED, COMPLETED
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EventStatus status = EventStatus.DRAFT;

    /**
     * ID de l'organisateur (référence vers User Service)
     * Stocké comme référence, pas de foreign key car dans un autre service
     */
    @Column(nullable = false)
    @NotNull(message = "L'organisateur est obligatoire")
    private Long organizerId;

    /**
     * URL de l'image de l'événement
     */
    @Column(length = 500)
    private String imageUrl;

    /**
     * Date de création de l'enregistrement
     * Automatiquement remplie lors de la création
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date de dernière modification
     * Automatiquement mise à jour
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Callback JPA appelé avant la persistance
     * Initialise les dates et les sièges disponibles
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (availableSeats == null) {
            availableSeats = capacity;
        }
    }

    /**
     * Callback JPA appelé avant la mise à jour
     * Met à jour la date de modification
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Vérifie si l'événement a encore des places disponibles
     */
    public boolean hasAvailableSeats() {
        return availableSeats != null && availableSeats > 0;
    }

    /**
     * Réserve un certain nombre de places
     * Retourne true si la réservation est possible
     */
    public boolean reserveSeats(int numberOfSeats) {
        if (availableSeats >= numberOfSeats) {
            availableSeats -= numberOfSeats;
            return true;
        }
        return false;
    }
}
