package com.event.platform.bookingservice.client;

import com.event.platform.bookingservice.dto.EventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Simple REST client to call Event Service endpoints.
 * Uses `EVENT_SERVICE_URL` environment variable (or default http://event-service:8080).
 */
@Component
public class RestEventServiceClient implements EventServiceClient {

    private static final Logger log = LoggerFactory.getLogger(RestEventServiceClient.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${EVENT_SERVICE_URL:http://event-service:8080}")
    private String eventServiceUrl;

    @Override
    public EventDTO getEventById(Long id) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(eventServiceUrl)
                    .path("/events/")
                    .path(String.valueOf(id))
                    .toUriString();
            ResponseEntity<EventDTO> res = restTemplate.getForEntity(url, EventDTO.class);
            return res.getBody();
        } catch (RestClientException e) {
            log.warn("Event Service unreachable for getEventById id={}. Returning null.", id, e);
            return null;
        }
    }

    @Override
    public Boolean reserveSeats(Long eventId, int numberOfSeats) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(eventServiceUrl)
                    .path("/events/")
                    .path(String.valueOf(eventId))
                    .path("/reserve")
                    .queryParam("numberOfSeats", numberOfSeats)
                    .toUriString();
            ResponseEntity<Boolean> res = restTemplate.postForEntity(url, null, Boolean.class);
            return Boolean.TRUE.equals(res.getBody());
        } catch (RestClientException e) {
            log.warn("Event Service unreachable for reserveSeats eventId={}. Returning false.", eventId, e);
            return false;
        }
    }

    @Override
    public void releaseSeats(Long eventId, int numberOfSeats) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(eventServiceUrl)
                    .path("/events/")
                    .path(String.valueOf(eventId))
                    .path("/release")
                    .queryParam("numberOfSeats", numberOfSeats)
                    .toUriString();
            restTemplate.postForEntity(url, null, Void.class);
        } catch (RestClientException e) {
            log.warn("Event Service unreachable for releaseSeats eventId={}.", eventId, e);
        }
    }
}
