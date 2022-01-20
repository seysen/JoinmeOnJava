package com.dataart.joinme.services;

import com.dataart.joinme.dto.ResponseUserDto;
import com.dataart.joinme.dto.UserDto;
import com.dataart.joinme.exceptions.EmptyFieldsException;
import com.dataart.joinme.exceptions.UserAlreadyExistException;
import com.dataart.joinme.exceptions.WrongEmailOrPasswordException;
import com.dataart.joinme.models.User;
import com.dataart.joinme.repositoryes.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
//@NoArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public ResponseUserDto registration(UserDto userDto) {
        logger.debug("Trying to register new user with request: {}", userDto);
        isRegistrationDtoCorrect(userDto);
        User userByDto = createUserByDto(userDto);
        logger.debug("Saving user: {}", userByDto);
        User user = userRepository.save(userByDto);
        ResponseUserDto response = createResponse(user.getId());
        logger.debug("Successful registration with response: {}", response);
        return response;
    }

    public ResponseUserDto login(UserDto userDto) {
        logger.debug("Trying authorize user: {}", userDto);
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
        isLoginDtoCorrect(userDto, optionalUser);
        ResponseUserDto response = createResponse(optionalUser.get().getId());
        logger.debug("Authorization successful with response: {}", response);
        return response;
    }

    private void isRegistrationDtoCorrect(UserDto userDto) {
        if (userDto.getFirstName().equals("") ||
                userDto.getLastName().equals("") ||
                userDto.getFirstName() == null ||
                userDto.getLastName() == null) {
            logger.debug("Some fields are empty or null {}", userDto);
            throw new EmptyFieldsException();
        }
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if (optionalUser.isPresent()) {
            String email = optionalUser.get().getEmail();
            logger.debug("User with email: {} already exist", email);
            throw new UserAlreadyExistException(email);
        }
    }

    private User createUserByDto(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        return user;
    }

    private ResponseUserDto createResponse(Long id) {
        long JWT_EXPIRATION_DAYS = 15L;
        String jwt = Jwts.builder()
                .setIssuer(id.toString())
                .setExpiration(Date.from(LocalDate.now()
                        .plusDays(JWT_EXPIRATION_DAYS).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, "secret").compact();
        return new ResponseUserDto(id, jwt);
    }

    private void isLoginDtoCorrect(UserDto userDto, Optional<User> optionalUser) {
        if (optionalUser.isEmpty() || !comparePassword(userDto.getPassword(), optionalUser.get().getPassword())) {
            logger.debug("Wrong email or password: {}", userDto);
            throw new WrongEmailOrPasswordException();
        }
    }

    private boolean comparePassword(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
