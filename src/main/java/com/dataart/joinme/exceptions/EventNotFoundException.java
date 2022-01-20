package com.dataart.joinme.exceptions;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(Long eventId) {
        super(String.format("Event with ID %d not found", eventId));
    }
}
