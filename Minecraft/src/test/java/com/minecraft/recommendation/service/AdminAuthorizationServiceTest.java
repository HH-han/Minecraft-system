package com.minecraft.recommendation.service;

import com.minecraft.entity.User;
import com.minecraft.exception.BusinessException;
import com.minecraft.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminAuthorizationServiceTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private AdminAuthorizationService authorizationService;

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    private void login(String principal) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, List.of()));
    }

    @Test
    void anonymous_throws401() {
        BusinessException ex = assertThrows(BusinessException.class, authorizationService::requireAdmin);
        assertEquals(401, ex.getCode());
    }

    @Test
    void userMissing_throws401() {
        login("42");
        when(userService.getById(42L)).thenReturn(null);
        BusinessException ex = assertThrows(BusinessException.class, authorizationService::requireAdmin);
        assertEquals(401, ex.getCode());
    }

    @Test
    void nonAdmin_throws403() {
        login("42");
        User user = new User();
        user.setId(42L);
        user.setPermissions("1");
        when(userService.getById(42L)).thenReturn(user);

        BusinessException ex = assertThrows(BusinessException.class, authorizationService::requireAdmin);
        assertEquals(403, ex.getCode());
    }

    @Test
    void admin_returnsUserId() {
        login("42");
        User user = new User();
        user.setId(42L);
        user.setPermissions(AdminAuthorizationService.ADMIN_PERMISSION);
        when(userService.getById(42L)).thenReturn(user);

        assertEquals(42L, authorizationService.requireAdmin());
    }
}
