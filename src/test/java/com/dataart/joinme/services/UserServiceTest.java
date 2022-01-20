package com.dataart.joinme.services;

import com.dataart.joinme.dto.ResponseUserDto;
import com.dataart.joinme.dto.UserDto;
import com.dataart.joinme.exceptions.UserAlreadyExistException;
import com.dataart.joinme.exceptions.WrongEmailOrPasswordException;
import com.dataart.joinme.models.User;
import com.dataart.joinme.repositoryes.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private UserService userService;

    @Test
    void successfulRegistrationTest() {
        UserDto userDto = new UserDto("Jhon","Doe","123@qwer.ty","qwerty");
        User user = new User(1L,"Jhon","Doe","123@qwer.ty","qwerty",null,null, Instant.now(),Instant.now());


        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(any(),any())).thenReturn(user);

        ResponseUserDto responseUserDto = userService.registration(userDto);

        assertNotNull(responseUserDto);
        assertEquals(responseUserDto.getUserId(),user.getId());
    }

    @Test
    void emailAlreadyExistRegistrationTest() {
        UserDto userDto = new UserDto("Jhon","Doe","123@qwer.ty","qwerty");
        User user = new User(1L,"Jhon","Doe","123@qwer.ty","qwerty",null,null, Instant.now(),Instant.now());

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistException.class,() -> {
            userService.registration(userDto);
        });
    }

    @Test
    void successfulLoginTest() {
        UserDto userDto = new UserDto(null,null,"123@qwer.ty","qwerty");
        User user = new User(1L,"Jhon","Doe","123@qwer.ty","qwerty",null,null, Instant.now(),Instant.now());

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(any(),any())).thenReturn(true);

        ResponseUserDto responseUserDto = userService.login(userDto);

        assertNotNull(responseUserDto);
        assertEquals(responseUserDto.getUserId(),user.getId());
    }

    @Test
    void wrongEmailLoginTest() {
        UserDto userDto = new UserDto(null,null,"123@qwer.ty","qwerty");

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());

        assertThrows(WrongEmailOrPasswordException.class,() -> {
            userService.login(userDto);
        });
    }

    @Test
    void wrongPasswordLoginTest() {
        UserDto userDto = new UserDto(null,null,"123@qwer.ty","1111");
        User user = new User(1L,"Jhon","Doe","123@qwer.ty","qwerty",null,null, Instant.now(),Instant.now());

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(any(),any())).thenReturn(false);

        assertThrows(WrongEmailOrPasswordException.class,() -> {
            userService.login(userDto);
        });
    }
}