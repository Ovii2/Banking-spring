package org.example.bankingapplication.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.bankingapplication.enums.TokenType;
import org.example.bankingapplication.model.Token;
import org.example.bankingapplication.model.User;
import org.example.bankingapplication.repository.TokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;


    private String jwtToken = "test-jwt-token";

    @Test
    @DisplayName("saveUserToken_Successful")
    void saveUserTokenSuccessful() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        tokenService.saveUserToken(user, jwtToken);

        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
        verify(tokenRepository, times(1)).save(tokenCaptor.capture());

        Token capturedToken = tokenCaptor.getValue();
        assertNotNull(capturedToken);
        assertEquals(user, capturedToken.getUser());
        assertEquals(jwtToken, capturedToken.getToken());
        assertEquals(TokenType.BEARER, capturedToken.getTokenType());
        assertFalse(capturedToken.isExpired());
        assertFalse(capturedToken.isRevoked());
    }

    @Test
    @DisplayName("saveUserToken_NullUser_Fail")
    void saveUserTokenNullUserFail() {
        assertThrows(IllegalArgumentException.class, () -> tokenService.saveUserToken(null, jwtToken));
        verifyNoInteractions(tokenRepository);

    }

    @Test
    @DisplayName("getCurrentToken_Successful")
    void getCurrentTokenSuccessful() {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        String extractedToken = tokenService.getCurrentToken();

        assertEquals(jwtToken, extractedToken);
    }

    @Test
    @DisplayName("getCurrentToken_Failure_NoHeader")
    void getCurrentTokenFailure_NoHeader() {
        when(request.getHeader("Authorization")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> tokenService.getCurrentToken());
    }

    @Test
    @DisplayName("revokeAllUserTokens_Successful")
    void revokeAllUserTokensSuccessful() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        Token token1 = Token.builder()
                .user(user).token("token1")
                .tokenType(TokenType.BEARER)
                .isExpired(false)
                .isRevoked(false)
                .build();

        Token token2 = Token.builder()
                .user(user)
                .token("token2")
                .tokenType(TokenType.BEARER)
                .isExpired(false)
                .isRevoked(false)
                .build();

        List<Token> userTokens = List.of(token1, token2);

        when(tokenRepository.findAllValidTokensByUser(user.getId())).thenReturn(userTokens);

        tokenService.revokeAllUserTokens(user);

        assertTrue(token1.isExpired());
        assertTrue(token1.isRevoked());
        assertTrue(token2.isExpired());
        assertTrue(token2.isRevoked());

        verify(tokenRepository, times(1)).saveAll(userTokens);
    }

    @Test
    @DisplayName("revokeAllUserTokens_Failure_NoValidTokens")
    void revokeAllUserTokensFailure_NoValidTokens() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        when(tokenRepository.findAllValidTokensByUser(user.getId())).thenReturn(Collections.emptyList());

        tokenService.revokeAllUserTokens(user);

        verify(tokenRepository, never()).saveAll(any());
    }
}