package com.example.ridehailing.aspect;

import com.example.ridehailing.annotation.RequiredRole;
import com.example.ridehailing.dto.UserDto;
import com.example.ridehailing.exception.UnauthorizedException;
import com.example.ridehailing.model.UserType;
import com.example.ridehailing.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class RoleAuthorizationAspect {

    private final AuthService authService;

    public RoleAuthorizationAspect(AuthService authService) {
        this.authService = authService;
    }

    @Before("@annotation(requiredRole)")
    public void checkRole(JoinPoint joinPoint, RequiredRole requiredRole) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new UnauthorizedException("Request attributes not found");
        }

        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }

        UserDto userDto = authService.validateToken(authHeader);
        UserType userRole = userDto.getUserType();

        boolean isAuthorized = Arrays.stream(requiredRole.value())
                .anyMatch(role -> role == userRole);

        if (!isAuthorized) {
            throw new UnauthorizedException("User does not have the required role");
        }
    }
}
