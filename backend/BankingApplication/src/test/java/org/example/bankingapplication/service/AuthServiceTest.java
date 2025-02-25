package org.example.bankingapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.bankingapplication.dto.login.LoginRequestDTO;
import org.example.bankingapplication.dto.login.LoginResponseDTO;
import org.example.bankingapplication.dto.register.RegisterRequestDTO;
import org.example.bankingapplication.dto.register.RegisterResponseDTO;
import org.example.bankingapplication.enums.Roles;
import org.example.bankingapplication.enums.TokenType;
import org.example.bankingapplication.exceptions.UserAlreadyExistsException;
import org.example.bankingapplication.exceptions.UserAlreadyLoggedInException;
import org.example.bankingapplication.model.Token;
import org.example.bankingapplication.model.User;
import org.example.bankingapplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Argon2PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenService tokenService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO registerRequestDTO;
    private LoginRequestDTO loginRequestDTO;
    private User user;


    @BeforeEach
    void setup() {
        user = User.builder()
                .id(UUID.randomUUID())
                .username("Test")
                .email("test@gmail.com")
                .role(Roles.ROLE_USER)
                .build();

        registerRequestDTO = RegisterRequestDTO.builder()
                .username("Test")
                .email("test@gmail.com")
                .password("password")
                .build();

        loginRequestDTO = LoginRequestDTO.builder()
                .username("test")
                .password("password")
                .build();

    }


    @Test
    @DisplayName("register_WhenUserDoesNotExist_ShouldRegister_Successfully")
    void registerWhenUserDoesNotExistShouldRegisterSuccessfully() throws UserAlreadyExistsException {
        when(userRepository.existsUserByEmail(registerRequestDTO.getEmail())).thenReturn(false);
        when(userRepository.existsUserByUsername(registerRequestDTO.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerRequestDTO.getPassword())).thenReturn("encodedPassword");

        RegisterResponseDTO registerResponseDTO = authService.register(registerRequestDTO);

        assertNotNull(registerResponseDTO);
        assertEquals("User registered successfully!", registerResponseDTO.getMessage());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register_WhenEmailExists_ShouldThrowException")
    void registerWhenEmailExistsShouldThrowException() {
        when(userRepository.existsUserByEmail(registerRequestDTO.getEmail())).thenReturn(false);
        when(userRepository.existsUserByUsername(registerRequestDTO.getUsername())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(registerRequestDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("login_WhenValidCredentials_ShouldLogin_Successfully")
    void loginWhenValidCredentialsShouldLoginSuccessfully() {
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        when(tokenService.getAllValidUserTokens(any(User.class))).thenReturn(new ArrayList<>());
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        LoginResponseDTO response = authService.login(loginRequestDTO);

        assertNotNull(response);
        assertEquals("User logged in successfully", response.getMessage());
        assertEquals("jwt-token", response.getToken());
        verify(tokenService).saveUserToken(any(User.class), anyString());
    }

    @Test
    @DisplayName("login_WhenUserAlreadyIsLoggedIn_ShouldThrowException")
    void loginWhenUserAlreadyIsLoggedInShouldThrowException() {

        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));

        List<Token> tokens = List.of(
                Token.builder()
                        .id(UUID.randomUUID())
                        .token("existing-token")
                        .tokenType(TokenType.BEARER)
                        .isExpired(false)
                        .isRevoked(false)
                        .build()
        );

        when(tokenService.getAllValidUserTokens(any(User.class))).thenReturn(tokens);
        assertThrows(UserAlreadyLoggedInException.class, () -> authService.login(loginRequestDTO));
        verify(tokenService, never()).saveUserToken(any(User.class), anyString());
    }

    @Test
    @DisplayName("login_WhenUserNotFound_ShouldThrowException")
    void loginWhenUserNotFoundShouldThrowException() {
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> authService.login(loginRequestDTO));
    }

    @Test
    @DisplayName("getCurrentUser_WhenUserIsAuthenticated_ShouldReturnUser")
    void getCurrentUserWhenUserIsAuthenticatedShouldReturnUser() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(user));

        Optional<User> result = authService.getCurrentUser();

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("generateAccountNumber_ShouldGenerateValidAccountNumber")
    void generateAccountNumberShouldGenerateValidAccountNumber() {
        String accountNumber1 = AuthService.generateAccountNumber();
        String accountNumber2 = AuthService.generateAccountNumber();

        assertNotNull(accountNumber1);
        assertNotNull(accountNumber2);

        assertEquals(16, accountNumber1.length());
        assertEquals(16, accountNumber2.length());

        assertNotEquals(accountNumber1, accountNumber2, "Account numbers should be unique");
    }

    @Test
    @DisplayName("getCurrentUser_WhenUserNotAuthenticated_ShouldReturnEmpty")
    void getCurrentUserWhenUserNotAuthenticatedShouldReturnEmpty() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        Optional<User> result = authService.getCurrentUser();

        assertTrue(result.isEmpty());
    }
}