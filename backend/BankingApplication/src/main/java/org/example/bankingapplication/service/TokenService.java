package org.example.bankingapplication.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.bankingapplication.enums.TokenType;
import org.example.bankingapplication.model.Token;
import org.example.bankingapplication.model.User;
import org.example.bankingapplication.repository.TokenRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {


    private final TokenRepository tokenRepository;
    private final HttpServletRequest request;

    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void deleteAllUserTokens(User user) {
        var allUserTokens = tokenRepository.findAllByUserId(user.getId());
        if (!allUserTokens.isEmpty()) {
            tokenRepository.deleteAll(allUserTokens);
        }
    }

    public List<Token> getAllValidUserTokens(User user) {
        return tokenRepository.findAllValidTokensByUserId(user.getId());
    }

    public String getCurrentToken() {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        } else {
            throw new IllegalArgumentException("No JWT token found in the request headers");
        }
    }

    public void clearToken() {
        SecurityContextHolder.clearContext();
    }
}
