package com.event.platform.eventservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import com.fasterxml.jackson.annotation.JsonAlias;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO implements Serializable {
    private Long id;

    @NotBlank(message = "name is required")
    private String name;

    private String description;

    @NotBlank(message = "location is required")
    private String location;

    @JsonAlias({"date"})
    @NotNull(message = "startDate is required")
    private OffsetDateTime startDate;

    @NotNull(message = "endDate is required")
    private OffsetDateTime endDate;

    @NotNull(message = "capacity is required")
    @Min(value = 1, message = "capacity must be at least 1")
    private Integer capacity;

    private Integer availableSeats;

    @NotNull(message = "basePrice is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "basePrice must be positive")
    private BigDecimal basePrice;

    // accept raw category string (e.g. "MUSIC") and map server-side
    @NotBlank(message = "category is required")
    private String category;
}
