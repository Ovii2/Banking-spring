package org.example.bankingapplication.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bankingapplication.dto.login.LoginRequestDTO;
import org.example.bankingapplication.dto.login.LoginResponseDTO;
import org.example.bankingapplication.dto.register.RegisterRequestDTO;
import org.example.bankingapplication.dto.register.RegisterResponseDTO;
import org.example.bankingapplication.exceptions.UserAlreadyExistsException;
import org.example.bankingapplication.exceptions.UserAlreadyLoggedInException;
import org.example.bankingapplication.exceptions.UserNotFoundException;
import org.example.bankingapplication.exceptions.UsernameOrPasswordInvalidException;
import org.example.bankingapplication.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        try {
            RegisterResponseDTO response = authService.register(registerRequestDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new RegisterResponseDTO(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RegisterResponseDTO(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            LoginResponseDTO loggedUser = authService.login(loginRequestDTO);
            return ResponseEntity.ok(loggedUser);
        } catch (UserAlreadyLoggedInException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new LoginResponseDTO(e.getMessage()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LoginResponseDTO(e.getMessage()));
        } catch (UsernameOrPasswordInvalidException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponseDTO(e.getMessage()));
        }
    }
}
