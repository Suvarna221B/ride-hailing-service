package com.example.ridehailing.aspect;

import com.example.ridehailing.annotation.RequiredRole;
import com.example.ridehailing.dto.UserDto;
import com.example.ridehailing.exception.UnauthorizedException;
import com.example.ridehailing.model.UserType;
import com.example.ridehailing.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleAuthorizationAspectTest {

    @Mock
    private AuthService authService;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private RequiredRole requiredRole;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletRequestAttributes requestAttributes;

    @InjectMocks
    private RoleAuthorizationAspect roleAuthorizationAspect;

    @BeforeEach
    public void setUp() {
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    @Test
    public void testCheckRole_Authorized() {
        when(requestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token");

        UserDto userDto = UserDto.builder()
                .userType(UserType.RIDER)
                .build();
        when(authService.validateToken("Bearer valid.token")).thenReturn(userDto);

        when(requiredRole.value()).thenReturn(new UserType[] { UserType.RIDER });

        assertDoesNotThrow(() -> roleAuthorizationAspect.checkRole(joinPoint, requiredRole));
    }

    @Test
    public void testCheckRole_Unauthorized_WrongRole() {
        when(requestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token");

        UserDto userDto = UserDto.builder()
                .userType(UserType.DRIVER)
                .build();
        when(authService.validateToken("Bearer valid.token")).thenReturn(userDto);

        when(requiredRole.value()).thenReturn(new UserType[] { UserType.RIDER });

        assertThrows(UnauthorizedException.class, () -> roleAuthorizationAspect.checkRole(joinPoint, requiredRole));
    }

    @Test
    public void testCheckRole_MissingHeader() {
        when(requestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("Authorization")).thenReturn(null);

        assertThrows(UnauthorizedException.class, () -> roleAuthorizationAspect.checkRole(joinPoint, requiredRole));
    }

    @Test
    public void testCheckRole_InvalidHeaderFormat() {
        when(requestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        assertThrows(UnauthorizedException.class, () -> roleAuthorizationAspect.checkRole(joinPoint, requiredRole));
    }
}
