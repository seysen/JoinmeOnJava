package com.dataart.joinme.controllers;

import com.dataart.joinme.dto.EventDto;
import com.dataart.joinme.models.Event;
import com.dataart.joinme.services.EventService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final ModelMapper modelMapper;

    private final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    public EventController(EventService eventService, ModelMapper modelMapper) {
        this.eventService = eventService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long add(@RequestBody EventDto eventRequest) {
        logger.debug("Request to add event: {}", eventRequest);
        Long eventId = eventService.addEvent(eventRequest);
        logger.debug("Event added with Id: {}", eventId);
        return eventId;
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable Long id) {
        logger.debug("Request to get event with id: {}", id);
        Event event = eventService.findById(id);
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        logger.debug("Response getting of event: {}", eventDto);
        return eventDto;
    }

    @GetMapping
    public List<EventDto> getEvents(@RequestParam Integer page) {
        logger.debug("Request to get page {} of events", page);
        List<Event> events = eventService.getEvents(page);
        List<EventDto> eventDtoList = events.stream().map(
                event -> modelMapper.map(event, EventDto.class)).collect(Collectors.toList());
        logger.debug("Response of getting page of events: {}", eventDtoList);
        return eventDtoList;
    }
}
