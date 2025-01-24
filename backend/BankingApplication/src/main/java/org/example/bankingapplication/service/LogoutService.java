package org.example.bankingapplication.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.bankingapplication.repository.TokenRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                response.getWriter().write("No JWT token found in the request headers");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        jwt = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(jwt).orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.delete(storedToken);
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_OK);
            try {
                response.getWriter().write("Logout successful");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                response.getWriter().write("Invalid JWT token");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
