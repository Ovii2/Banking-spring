package org.example.bankingapplication.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bankingapplication.dto.transaction.TransactionRequestDTO;
import org.example.bankingapplication.dto.transaction.TransactionResponseDTO;
import org.example.bankingapplication.exceptions.AccountNotFoundException;
import org.example.bankingapplication.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/account/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDTO> depositFunds(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO) {
        try {
            TransactionResponseDTO response = transactionService.deposit(transactionRequestDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TransactionResponseDTO(e.getMessage()));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new TransactionResponseDTO(e.getMessage()));
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdrawFunds(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO) {
        try {
            TransactionResponseDTO response = transactionService.withdraw(transactionRequestDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TransactionResponseDTO(e.getMessage()));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new TransactionResponseDTO(e.getMessage()));
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transferFunds(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO) {
        try {
            TransactionResponseDTO response = transactionService.transfer(transactionRequestDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TransactionResponseDTO(e.getMessage()));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new TransactionResponseDTO(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponseDTO>> getUserTransactions(@PathVariable UUID userId) {
        try {
            List<TransactionResponseDTO> response = transactionService.getAllTransactionsByUserId(userId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonList(new TransactionResponseDTO(e.getMessage())));
        }
    }
}
