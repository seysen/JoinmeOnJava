package com.dataart.joinme.controllers;

import com.amazonaws.util.json.Jackson;
import com.dataart.joinme.dto.EventDto;
import com.dataart.joinme.exceptions.EventNotFoundException;
import com.dataart.joinme.exceptions.UserNotFoundException;
import com.dataart.joinme.models.Event;
import com.dataart.joinme.models.User;
import com.dataart.joinme.services.EventService;
import com.dataart.joinme.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@WebMvcTest(EventController.class)
@RunWith(SpringRunner.class)
class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private UserService userService;

    @MockBean
    private ModelMapper modelMapper;

    @Test
    void successfulAddEventTest() throws Exception {
        EventDto eventDto = new EventDto(null,"event name", null,
                "description", null,1L);
        when(eventService.addEvent(any())).thenReturn(3L);

        MockHttpServletResponse servletResponse = mockMvc.perform(
                        MockMvcRequestBuilders.post("/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Jackson.toJsonString(eventDto)))
                .andReturn().getResponse();
        int status = servletResponse.getStatus();
        String response = servletResponse.getContentAsString();

        assertEquals(HttpStatus.CREATED.value(),status);
        assertEquals("3",response);
    }

    @Test
    void addEventWithNotExistedUserTest() {
        EventDto eventDto = new EventDto(null,"event name", null,
                "description", null,4L);
        when(eventService.addEvent(any())).thenThrow(UserNotFoundException.class);

        assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.post("/events")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(Jackson.toJsonString(eventDto)))
                    .andReturn().getResponse();
        });
    }

    @Test
    void getEventTest() throws Exception {
        EventDto eventDto = new EventDto(3L,"event name", null,
                "description", null,1L);
        Event event = new Event("event name","description",null,null, new User());
        event.setId(3L);

        when(eventService.findById(any())).thenReturn(event);
        when(modelMapper.map(any(),any())).thenReturn(eventDto);

        MockHttpServletResponse servletResponse = mockMvc.perform(
                        MockMvcRequestBuilders.get("/events/3")
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(),servletResponse.getStatus());
        assertEquals(eventDto, Jackson.fromJsonString(servletResponse.getContentAsString(), EventDto.class));
    }

    @Test
    void getEventNegativeTest() {
        doThrow(EventNotFoundException.class).when(eventService).findById(any());

        assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/events/0")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
        });
    }

    @Test
    void getEvents() throws Exception {
        Event event = new Event("Event name", "Event description",
            null, null, new User());
        EventDto eventDto = new EventDto(3L,"Event name", null ,
                "Event description", null, 1L);
        ArrayList<Event> events = new ArrayList<>();
        ArrayList<EventDto> eventDtos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            events.add(event);
            eventDtos.add(eventDto);
        }
        when(eventService.getEvents(anyInt())).thenReturn(events);
        when(modelMapper.map(event,EventDto.class)).thenReturn(eventDto);

        MockHttpServletResponse servletResponse = mockMvc.perform(
                        MockMvcRequestBuilders.get("/events")
                                .param("page","0")
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(),servletResponse.getStatus());
        assertEquals(10,Jackson.fromJsonString(servletResponse.getContentAsString(),eventDtos.getClass()).size());
    }
}
