package com.event.platform.bookingservice.client;

import com.event.platform.bookingservice.dto.EventDTO;

/**
 * Interface du client vers Event Service utilisée par le Booking Service.
 * Contient les signatures nécessaires au fallback.
 */
public interface EventServiceClient {

    EventDTO getEventById(Long id);

    Boolean reserveSeats(Long eventId, int numberOfSeats);

    void releaseSeats(Long eventId, int numberOfSeats);
}
