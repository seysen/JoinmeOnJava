package com.dataart.joinme.controllers;

import com.amazonaws.util.json.Jackson;
import com.dataart.joinme.dto.ResponseUserDto;
import com.dataart.joinme.dto.UserDto;
import com.dataart.joinme.exceptions.UserAlreadyExistException;
import com.dataart.joinme.exceptions.WrongEmailOrPasswordException;
import com.dataart.joinme.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(AuthController.class)
@RunWith(SpringRunner.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void successfulRegistrationTest() throws Exception {
        UserDto userDto = new UserDto();
        ResponseUserDto responseUserDto = new ResponseUserDto(1L,"jwt");
        when(userService.registration(any())).thenReturn(responseUserDto);

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Jackson.toJsonString(userDto)))
                .andReturn().getResponse();
        int status = response.getStatus();
        ResponseUserDto responseFromJson = Jackson.fromJsonString(
                response.getContentAsString(),
                ResponseUserDto.class);

        assertEquals(HttpStatus.CREATED.value(), status);
        assertEquals(responseUserDto, responseFromJson);
    }

    @Test
    void registrationWithTheSameEmailTest() throws Exception {
        UserDto userDto = new UserDto();
        when(userService.registration(any())).thenThrow(UserAlreadyExistException.class);

        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Jackson.toJsonString(userDto)))
                .andReturn().getResponse();
        int status = response.getStatus();

        assertEquals(HttpStatus.CONFLICT.value(),status);
    }

    @Test
    void successfulLoginTest() throws Exception {
        UserDto userDto = new UserDto();
        ResponseUserDto responseUserDto = new ResponseUserDto(1L,"jwt");
        when(userService.login(any())).thenReturn(responseUserDto);

        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Jackson.toJsonString(userDto)))
                .andReturn().getResponse();
        int status = response.getStatus();
        ResponseUserDto responseFromJson = Jackson.fromJsonString(
                response.getContentAsString(),
                ResponseUserDto.class);

        assertEquals(HttpStatus.OK.value(), status);
        assertEquals(responseUserDto, responseFromJson);
    }

    @Test
    void loginWithWrongEmailOrPasswordTest() throws Exception {
        UserDto userDto = new UserDto();
        when(userService.login(any())).thenThrow(WrongEmailOrPasswordException.class);

        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Jackson.toJsonString(userDto)))
                .andReturn().getResponse();
        int status = response.getStatus();

        assertEquals(HttpStatus.UNAUTHORIZED.value(), status);
    }
}