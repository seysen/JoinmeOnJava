package com.dataart.joinme.services;

import com.dataart.joinme.dto.EventDto;
import com.dataart.joinme.exceptions.EventNotFoundException;
import com.dataart.joinme.exceptions.UserNotFoundException;
import com.dataart.joinme.models.Event;
import com.dataart.joinme.models.User;
import com.dataart.joinme.repositoryes.EventRepository;
import com.dataart.joinme.repositoryes.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventService eventService;

    @Test
    void addEventTest() {
        User user = new User(1L,"Jhon","Doe","123@qwer.ty",
                "qwerty",null,null, Instant.now(),Instant.now());
        EventDto eventDto = new EventDto(null, "New event", Instant.now(),
                "New event description",null,1L);
        Event event = new Event("New event", "New event description",
                Instant.now(),null, user);
        event.setId(3L);

        given(userRepository.existsById(any())).willReturn(true);
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(eventRepository.save(any())).willReturn(event);

        Long id = eventService.addEvent(eventDto);

        assertEquals(3L,id);
    }

    @Test
    void addEventByUnknownUserTest() {
        EventDto eventDto = new EventDto(null, "New event", Instant.now(),
            "New event description",null,1L);

        given(userRepository.existsById(any())).willReturn(false);

        assertThrows(UserNotFoundException.class,() -> {
            eventService.addEvent(eventDto);
        });
    }

    @Test
    void findByIdPositiveTest() {
        Event event = new Event("New event", "New event description",
                Instant.now(),null, null);
        event.setId(3L);
        Long id = 3L;

        given(eventRepository.findById(id)).willReturn(Optional.of(event));

        Event eventById = eventService.findById(id);

        assertEquals(id,eventById.getId());
    }

    @Test
    void findByIdNegativeTest() {
        Long id = 3L;

        given(eventRepository.findById(any())).willReturn(Optional.empty());

        assertThrows(EventNotFoundException.class,() -> {
            eventService.findById(id);
        });
    }

    @Test
    void getEventsTest() {
        List<Event> eventList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            eventList.add(new Event());
        }

        given(eventRepository.findEventsByDateAfter(any(),any())).willReturn(new PageImpl<>(eventList));

        List<Event> events = eventService.getEvents(0);

        assertEquals(10, events.size());
        assertEquals(Event.class, events.get(0).getClass());
    }
}