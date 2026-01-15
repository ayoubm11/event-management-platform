package com.event.platform.bookingservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class BookingRequest {
    @NotNull(message = "eventId is required")
    private Long eventId;

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "numberOfTickets is required")
    @Min(value = 1, message = "At least one ticket is required")
    private Integer numberOfTickets;

    @NotNull(message = "totalPrice is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be positive")
    private BigDecimal totalPrice;

    @NotBlank(message = "userEmail is required")
    @Email(message = "userEmail must be a valid email")
    private String userEmail;

    @NotBlank(message = "eventName is required")
    private String eventName;

    @NotNull(message = "eventDate is required")
    private LocalDateTime eventDate;

    private String notes;
}
