package org.example.bankingapplication.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankingapplication.dto.login.LoginRequestDTO;
import org.example.bankingapplication.dto.login.LoginResponseDTO;
import org.example.bankingapplication.dto.register.RegisterRequestDTO;
import org.example.bankingapplication.dto.register.RegisterResponseDTO;
import org.example.bankingapplication.exceptions.UserNotFoundException;
import org.example.bankingapplication.repository.TokenRepository;
import org.example.bankingapplication.service.AuthService;
import org.example.bankingapplication.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("registerUser_ShouldReturnCreated_WhenSuccessful")
    void registerUserShouldReturnCreatedWhenSuccessful() throws Exception {

        RegisterRequestDTO registerRequest = RegisterRequestDTO
                .builder()
                .username("TestUser")
                .email("test@email.com")
                .password("password")
                .build();

        RegisterResponseDTO response = new RegisterResponseDTO("User registered successfully!");

        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    @DisplayName("registerUser_ShouldReturnBadRequest_WhenValidationFails")
    void registerUserShouldReturnBadRequestWhenValidationFails() throws Exception {

        RegisterRequestDTO registerRequestDTO = RegisterRequestDTO.builder()
                .username("")
                .email("email@email.com")
                .password("password")
                .build();

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequestDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("loginUser_ShouldReturnOk_WhenCredentialsValid")
    void loginUserShouldReturnOkWhenCredentialsValid() throws Exception {

        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .username("TestUser")
                .password("password")
                .build();

        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .token("jwt-token")
                .message("User logged in successfully")
                .build();

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(loginResponseDTO);

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestDTO))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User logged in successfully"));
    }

    @Test
    @DisplayName("loginUser_ShouldReturnBadRequest_WhenCredentialsInvalid")
    void loginUserShouldReturnBadRequestWhenCredentialsInvalid() throws Exception {
        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .username("NonExisting")
                .password("password")
                .build();

        when(authService.login(any(LoginRequestDTO.class))).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestDTO))
                )
                .andDo(print())
                .andExpect(status().isNotFound());

    }
}