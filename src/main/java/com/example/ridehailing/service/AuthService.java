package com.example.ridehailing.service;

import com.example.ridehailing.dto.LoginResponseDto;
import com.example.ridehailing.dto.UserDto;
import com.example.ridehailing.dto.UserRequestDto;
import com.example.ridehailing.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public AuthService(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public LoginResponseDto login(UserRequestDto userRequestDto) {
        UserDto userDto = userService.verifyUser(userRequestDto);

        if (userDto == null) {
            throw new UnauthorizedException("User not authorized");
        }

        return LoginResponseDto.builder().token(jwtService.generateToken(userDto)).build();
    }

    public UserDto validateToken(String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("Invalid authorization header");
            }
            
            String token = authorizationHeader.replace("Bearer ", "");
            return jwtService.getUserFromToken(token);
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid or expired token");
        }
    }
}
