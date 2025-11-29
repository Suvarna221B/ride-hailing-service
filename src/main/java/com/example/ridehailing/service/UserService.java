package com.example.ridehailing.service;

import com.example.ridehailing.dto.UserDto;
import com.example.ridehailing.dto.UserRequestDto;
import com.example.ridehailing.model.User;
import com.example.ridehailing.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto createUser(UserRequestDto userRequestDto) {
        User user = new User();
        user.setUsername(userRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setUserType(userRequestDto.getUserType());

        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }

    public UserDto verifyUser(UserRequestDto userRequestDto) {
        Optional<User> userOptional = userRepository.findByUsername(userRequestDto.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())) {
                return UserDto.fromEntity(user);
            }
        }
        return null;
    }
}
