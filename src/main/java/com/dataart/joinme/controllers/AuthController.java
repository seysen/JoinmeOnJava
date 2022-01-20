package com.dataart.joinme.controllers;


import com.dataart.joinme.dto.ResponseUserDto;
import com.dataart.joinme.dto.UserDto;
import com.dataart.joinme.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/register")
    public ResponseUserDto registration(@RequestBody UserDto userDto) {
        logger.debug("Registration request: {}", userDto);
        ResponseUserDto responseUserDto = userService.registration(userDto);
        logger.debug("Registration response: {}", responseUserDto);
        return responseUserDto;
    }

    @PostMapping("/login")
    public ResponseUserDto login(@RequestBody UserDto userDto) {
        logger.debug("Login request: {}", userDto);
        ResponseUserDto responseUserDto = userService.login(userDto);
        logger.debug("Login response: {}", responseUserDto);
        return responseUserDto;
    }
}
