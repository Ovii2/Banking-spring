package org.example.bankingapplication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.bankingapplication.enums.Roles;
import org.example.bankingapplication.model.User;
import org.example.bankingapplication.userDetails.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private final String secretKey = "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY=";
    private final long jwtExpiration = 3600000L;
    private final long refreshExpiration = 86400000L;

    private User user;
    private UserDetails userDetails;
    private String token;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", refreshExpiration);

        user = User.builder()
                .id(UUID.randomUUID())
                .username("test-user")
                .email("user@gmail.com")
                .accountNumber("LT12345678900000")
                .role(Roles.ROLE_USER)
                .build();

        userDetails = new CustomUserDetails(user);

        token = jwtService.generateToken(user);
    }


    @Test
    @DisplayName("generateToken_Successful")
    void generateTokenSuccessful() {
        String token = jwtService.generateToken(user);
        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        assertEquals(user.getUsername(), username);

        Claims claims = jwtService.extractAllClaims(token);
        assertEquals(user.getId(), UUID.fromString(claims.get("id", String.class)));
        assertEquals("test-user", claims.get("username", String.class));
        assertEquals("user@gmail.com", claims.get("email", String.class));
        assertEquals("LT12345678900000", claims.get("account_number", String.class));
        assertEquals(Roles.ROLE_USER.name(), claims.get("role", String.class));
    }

    @Test
    @DisplayName("extractUsername_Successfully")
    void extractUsernameSuccessfully() {
        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        assertEquals(user.getUsername(), username);
    }

    @Test
    @DisplayName("validateToken_Successfully")
    void validateTokenSuccessfully() {
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    @DisplayName("detect_Expired_Token")
    void detectExpiredToken() {
        String expiredToken = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() - jwtExpiration))
                .setExpiration(new Date(System.currentTimeMillis() - (jwtExpiration / 2)))
                .signWith(jwtService.getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenExpired(expiredToken));
    }

    @Test
    @DisplayName("extractClaims_ShouldReturn_CorrectValue")
    void extractClaimsShouldReturnCorrectValue() {
        Claims claims = jwtService.extractAllClaims(token);
        assertNotNull(claims);
        assertEquals(user.getUsername(), claims.getSubject());
    }

    @Test
    void getSignInKey_ShouldReturnNonNullKey() {
        Key key = jwtService.getSignInKey();
        assertNotNull(key);
    }
}