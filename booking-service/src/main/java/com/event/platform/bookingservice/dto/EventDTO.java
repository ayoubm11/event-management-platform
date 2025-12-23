package com.event.platform.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO minimal pour représenter un événement dans le Booking Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO implements Serializable {
    private Long id;
    private String name;
    private String location;
    private Integer availableSeats;
}
