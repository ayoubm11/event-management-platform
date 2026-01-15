package com.event.platform.bookingservice.model;

/**
 * Énumération des statuts de réservation
 */
public enum BookingStatus {
    PENDING,    // En attente de confirmation
    CONFIRMED,  // Confirmée et payée
    CANCELLED,  // Annulée
    REFUNDED    // Remboursée
}
