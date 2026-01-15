package com.event.platform.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private String bookingCode;
    private Long eventId;
    private Long userId;
    private Integer numberOfTickets;
    private BigDecimal totalPrice;
    private String status;
    private String userEmail;
    private String eventName;
    private LocalDateTime eventDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
