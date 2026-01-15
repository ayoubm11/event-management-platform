package com.event.platform.bookingservice.service;

import com.event.platform.bookingservice.client.EventServiceClient;
import com.event.platform.bookingservice.dto.BookingRequest;
import com.event.platform.bookingservice.dto.BookingResponse;
import com.event.platform.bookingservice.model.Booking;
import com.event.platform.bookingservice.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventServiceClient eventServiceClient;

    @Autowired
    public BookingService(BookingRepository bookingRepository, EventServiceClient eventServiceClient) {
        this.bookingRepository = bookingRepository;
        this.eventServiceClient = eventServiceClient;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest req) {
        // 1. Reserve seats on Event Service
        boolean reserved = eventServiceClient.reserveSeats(req.getEventId(), req.getNumberOfTickets());
        if (!reserved) {
            throw new IllegalStateException("Unable to reserve seats on Event Service");
        }

        // 2. Create booking
        Booking booking = Booking.builder()
                .eventId(req.getEventId())
                .userId(req.getUserId())
                .numberOfTickets(req.getNumberOfTickets())
                .userEmail(req.getUserEmail())
                .eventName(req.getEventName())
                .eventDate(req.getEventDate())
                .totalPrice(req.getTotalPrice() == null ? BigDecimal.ZERO : req.getTotalPrice())
                .notes(req.getNotes())
                .build();

        Booking saved = bookingRepository.save(booking);

        return toResponse(saved);
    }

    public BookingResponse getBooking(Long id) {
        return bookingRepository.findById(id).map(this::toResponse).orElse(null);
    }

    public List<BookingResponse> getAll() {
        return bookingRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!booking.canBeCancelled()) {
            throw new IllegalStateException("Booking cannot be cancelled");
        }

        // release seats on Event Service
        eventServiceClient.releaseSeats(booking.getEventId(), booking.getNumberOfTickets());
        booking.cancel();
        Booking saved = bookingRepository.save(booking);
        return toResponse(saved);
    }

    private BookingResponse toResponse(Booking b) {
        return BookingResponse.builder()
                .id(b.getId())
                .bookingCode(b.getBookingCode())
                .eventId(b.getEventId())
                .userId(b.getUserId())
                .numberOfTickets(b.getNumberOfTickets())
                .totalPrice(b.getTotalPrice())
                .status(b.getStatus().name())
                .userEmail(b.getUserEmail())
                .eventName(b.getEventName())
                .eventDate(b.getEventDate())
                .notes(b.getNotes())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }
}
