package com.event.platform.bookingservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité Booking - Représente une réservation
 *
 * Cette entité gère les réservations de billets pour les événements.
 * Elle maintient la cohérence entre le nombre de billets réservés
 * et la capacité disponible de l'événement.
 */
@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    /**
     * Identifiant unique de la réservation
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Code de réservation unique (ex: BK-20231215-001)
     * Utilisé pour identifier la réservation côté utilisateur
     */
    @Column(nullable = false, unique = true, length = 50)
    private String bookingCode;

    /**
     * ID de l'événement réservé
     * Référence vers Event Service (pas de FK car microservice séparé)
     */
    @Column(nullable = false)
    private Long eventId;

    /**
     * ID de l'utilisateur qui fait la réservation
     * Référence vers User Service
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * Nombre de billets réservés
     */
    @Column(nullable = false)
    private Integer numberOfTickets;

    /**
     * Prix total de la réservation
     * Calculé: basePrice * numberOfTickets + frais éventuels
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    /**
     * Statut de la réservation
     * PENDING, CONFIRMED, CANCELLED, REFUNDED
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    /**
     * Email de l'utilisateur (dénormalisé pour faciliter les notifications)
     */
    @Column(nullable = false)
    private String userEmail;

    /**
     * Nom de l'événement (dénormalisé pour l'affichage)
     */
    @Column(nullable = false)
    private String eventName;

    /**
     * Date de l'événement (dénormalisé)
     */
    @Column(nullable = false)
    private LocalDateTime eventDate;

    /**
     * Notes ou demandes spéciales
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Date de création de la réservation
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date de dernière modification
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Date de confirmation du paiement
     */
    private LocalDateTime confirmedAt;

    /**
     * Date d'annulation (si applicable)
     */
    private LocalDateTime cancelledAt;

    /**
     * Callback avant persistance
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Génère un code de réservation unique si non fourni
        if (bookingCode == null) {
            bookingCode = generateBookingCode();
        }
    }

    /**
     * Callback avant mise à jour
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Génère un code de réservation unique
     * Format: BK-YYYYMMDD-XXX
     */
    private String generateBookingCode() {
        String date = LocalDateTime.now().toString()
                .substring(0, 10).replace("-", "");
        int random = (int) (Math.random() * 10000);
        return String.format("BK-%s-%04d", date, random);
    }

    /**
     * Confirme la réservation
     */
    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    /**
     * Annule la réservation
     */
    public void cancel() {
        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    /**
     * Vérifie si la réservation peut être annulée
     */
    public boolean canBeCancelled() {
        return status == BookingStatus.CONFIRMED ||
                status == BookingStatus.PENDING;
    }
}

/**
 * Énumération des statuts de réservation
 */
enum BookingStatus {
    PENDING,    // En attente de confirmation
    CONFIRMED,  // Confirmée et payée
    CANCELLED,  // Annulée
    REFUNDED    // Remboursée
}