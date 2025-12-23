package com.event.platform.eventservice.service;

import com.event.platform.eventservice.dto.EventDTO;
import com.event.platform.eventservice.model.EventCategory;

import java.util.List;

public interface EventService {
    EventDTO createEvent(EventDTO eventDTO);
    List<EventDTO> getAllEvents();
    EventDTO getEventById(Long id);
    List<EventDTO> getAvailableEvents();
    List<EventDTO> searchEvents(String keyword);
    List<EventDTO> getEventsByCategory(EventCategory category);
    EventDTO updateEvent(Long id, EventDTO eventDTO);
    EventDTO publishEvent(Long id);
    EventDTO cancelEvent(Long id);
    boolean reserveSeats(Long id, int numberOfSeats);
    void releaseSeats(Long id, int numberOfSeats);
    void deleteEvent(Long id);
}
