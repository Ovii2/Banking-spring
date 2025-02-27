package org.example.bankingapplication.controllers;

import lombok.RequiredArgsConstructor;
import org.example.bankingapplication.dto.account.AccountResponseDTO;
import org.example.bankingapplication.exceptions.AccountNotFoundException;
import org.example.bankingapplication.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{userId}")
    public ResponseEntity<AccountResponseDTO> getAccountInformation(@PathVariable UUID userId) {
        try {
            AccountResponseDTO response = accountService.getAccountByUserId(userId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AccountResponseDTO(e.getMessage()));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AccountResponseDTO(e.getMessage()));
        }
    }
}
