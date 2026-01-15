package com.event.platform.eventservice.service;

import com.event.platform.eventservice.dto.EventDTO;
import com.event.platform.eventservice.model.Event;
import com.event.platform.eventservice.model.EventCategory;
import com.event.platform.eventservice.model.EventStatus;
import com.event.platform.eventservice.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public EventDTO createEvent(EventDTO eventDTO) {
        Event entity = toEntity(eventDTO);
        if (entity.getStatus() == null) {
            entity.setStatus(EventStatus.DRAFT);
        }
        Event saved = eventRepository.save(entity);
        return toDTO(saved);
    }

    @Override
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public EventDTO getEventById(Long id) {
        Event e = eventRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        return toDTO(e);
    }

    @Override
    public List<EventDTO> getAvailableEvents() {
        return eventRepository.findAvailableEvents(LocalDateTime.now()).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> searchEvents(String keyword) {
        return eventRepository.searchEvents(keyword).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> getEventsByCategory(EventCategory category) {
        return eventRepository.findByCategory(category).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventDTO updateEvent(Long id, EventDTO eventDTO) {
        Event existing = eventRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        if (eventDTO.getName() != null) existing.setName(eventDTO.getName());
        if (eventDTO.getDescription() != null) existing.setDescription(eventDTO.getDescription());
        if (eventDTO.getLocation() != null) existing.setLocation(eventDTO.getLocation());
        if (eventDTO.getStartDate() != null) existing.setStartDate(eventDTO.getStartDate().toLocalDateTime());
        if (eventDTO.getEndDate() != null) existing.setEndDate(eventDTO.getEndDate().toLocalDateTime());
        if (eventDTO.getCapacity() != null) existing.setCapacity(eventDTO.getCapacity());
        if (eventDTO.getAvailableSeats() != null) existing.setAvailableSeats(eventDTO.getAvailableSeats());
        if (eventDTO.getBasePrice() != null) existing.setBasePrice(eventDTO.getBasePrice());
        Event saved = eventRepository.save(existing);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public EventDTO publishEvent(Long id) {
        Event e = eventRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        e.setStatus(EventStatus.PUBLISHED);
        Event saved = eventRepository.save(e);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public EventDTO cancelEvent(Long id) {
        Event e = eventRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        e.setStatus(EventStatus.CANCELLED);
        Event saved = eventRepository.save(e);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public boolean reserveSeats(Long id, int numberOfSeats) {
        Optional<Event> opt = eventRepository.findById(id);
        if (opt.isEmpty()) return false;
        Event e = opt.get();
        boolean reserved = e.reserveSeats(numberOfSeats);
        if (reserved) eventRepository.save(e);
        return reserved;
    }

    @Override
    @Transactional
    public void releaseSeats(Long id, int numberOfSeats) {
        Event e = eventRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        e.setAvailableSeats(e.getAvailableSeats() + numberOfSeats);
        eventRepository.save(e);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    private EventDTO toDTO(Event e) {
        if (e == null) return null;
        return new EventDTO(
            e.getId(),
            e.getName(),
            e.getDescription(),
            e.getLocation(),
            e.getStartDate() != null ? e.getStartDate().atOffset(ZoneOffset.UTC) : null,
            e.getEndDate() != null ? e.getEndDate().atOffset(ZoneOffset.UTC) : null,
            e.getCapacity(),
            e.getAvailableSeats(),
            e.getBasePrice(),
            e.getCategory() != null ? e.getCategory().name() : null
        );
    }

    private Event toEntity(EventDTO dto) {
        if (dto == null) return null;
        // Map category string (from DTO) to enum, default to CULTURE/AUTRE on unknown
        EventCategory category = EventCategory.CULTURE;
        if (dto.getCategory() != null && !dto.getCategory().isBlank()) {
            try {
                category = EventCategory.valueOf(dto.getCategory().toUpperCase());
            } catch (IllegalArgumentException ex) {
                category = EventCategory.AUTRE;
            }
        }

        // Ensure startDate is present (could be mapped from 'date' via @JsonAlias)
        OffsetDateTime odtStart = dto.getStartDate();
        OffsetDateTime odtEnd = dto.getEndDate();
        LocalDateTime start = odtStart != null ? odtStart.toLocalDateTime() : null;
        LocalDateTime end = odtEnd != null ? odtEnd.toLocalDateTime() : null;
        if (start == null && end != null) start = end.minusHours(2);
        if (end == null && start != null) end = start.plusHours(2);

        Event.EventBuilder builder = Event.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .startDate(start)
                .endDate(end)
                .capacity(dto.getCapacity() == null ? 0 : dto.getCapacity())
                .availableSeats(dto.getAvailableSeats())
                .basePrice(dto.getBasePrice())
                .status(EventStatus.DRAFT)
                .category(category)
                .organizerId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now());
        if (dto.getId() != null) builder.id(dto.getId());
        return builder.build();
    }
}
