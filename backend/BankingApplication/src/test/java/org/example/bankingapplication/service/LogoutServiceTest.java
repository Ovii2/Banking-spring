package org.example.bankingapplication.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.bankingapplication.model.Token;
import org.example.bankingapplication.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.PrintWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private Authentication authentication;

    @Mock
    private PrintWriter printWriter;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private LogoutService logoutService;

    private Token token;
    private final String tokenValue = "valid_jwt_token";

    @BeforeEach
    void setup() throws Exception {
        token = Token.builder()
                .token(tokenValue)
                .isExpired(false)
                .isRevoked(false)
                .build();

        when(httpServletResponse.getWriter()).thenReturn(printWriter);
        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    @WithMockUser
    @DisplayName("logoutUser_WhenUserIsLoggedIn_Successfully")
    void logoutUserWhenUserIsLoggedInSuccessfully() {
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + tokenValue);
        when(tokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

        logoutService.logout(httpServletRequest, httpServletResponse, authentication);

        verify(tokenRepository).delete(token);
        verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write("Logout successful");

        assertTrue(token.isExpired());
        assertTrue(token.isRevoked());
    }

    @Test
    @DisplayName("logout_WhenTokenNotFound_ReturnsBadRequest")
    void logoutWhenTokenNotFoundReturnsBadRequest() {
        String invalidToken = "nonExistentToken";

        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(tokenRepository.findByToken(invalidToken)).thenReturn(Optional.empty());

        logoutService.logout(httpServletRequest, httpServletResponse, authentication);

        verify(tokenRepository).findByToken(invalidToken);
        verify(httpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(printWriter).write("Invalid JWT token");
    }
}