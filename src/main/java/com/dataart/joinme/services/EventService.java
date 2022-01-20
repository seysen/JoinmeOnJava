package com.dataart.joinme.services;

import com.dataart.joinme.dto.EventDto;
import com.dataart.joinme.exceptions.EventNotFoundException;
import com.dataart.joinme.exceptions.UserNotFoundException;
import com.dataart.joinme.models.Event;
import com.dataart.joinme.models.User;
import com.dataart.joinme.repositoryes.EventRepository;
import com.dataart.joinme.repositoryes.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private final int getEventCount = 10;
    private final Logger logger = LoggerFactory.getLogger(EventService.class);
    private final Pageable pageable = Pageable.ofSize(getEventCount);

    @Autowired
    public EventService(
            EventRepository eventRepository,
            UserRepository userRepository,
            ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public Long addEvent(EventDto eventDto) {
        Long creatorId = eventDto.getCreatorId();
        if (isUserExist(creatorId)) {
            User user = userRepository.findById(creatorId).get();
            Event event = eventRepository.save(modelMapper.map(eventDto, Event.class));
            logger.debug("Event added {}", event);
            return event.getId();
        }
        logger.debug("User with ID={} not found", creatorId);
        throw new UserNotFoundException(creatorId);
    }

    public Event findById(Long id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            logger.debug("Event with ID={} not found", id);
            throw new EventNotFoundException(id);
        }
        Event event = optionalEvent.get();
        logger.debug("Event found: {}", event);
        return event;
    }

    public List<Event> getEvents(int page) {
        List<Event> events = eventRepository.findEventsByDateAfter(Instant.now(), pageable.withPage(page)).toList();
        logger.debug("Events found: {}", events);
        return events;
    }

    private boolean isUserExist(Long id) {
        return userRepository.existsById(id);
    }
}
