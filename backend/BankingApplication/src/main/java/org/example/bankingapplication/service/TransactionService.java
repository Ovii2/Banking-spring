package org.example.bankingapplication.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.bankingapplication.dto.transaction.TransactionRequestDTO;
import org.example.bankingapplication.dto.transaction.TransactionResponseDTO;
import org.example.bankingapplication.enums.TransactionType;
import org.example.bankingapplication.exceptions.AccountNotFoundException;
import org.example.bankingapplication.model.Account;
import org.example.bankingapplication.model.Transaction;
import org.example.bankingapplication.repository.AccountRepository;
import org.example.bankingapplication.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    @Transactional
    public TransactionResponseDTO deposit(TransactionRequestDTO transactionRequestDTO) {
        if (transactionRequestDTO.getAmount() == null || transactionRequestDTO.getAmount() <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }

        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(transactionRequestDTO.getAccountNumber());
        if (optionalAccount.isEmpty()) {
            throw new AccountNotFoundException("Account not found");
        }
        Account account = optionalAccount.get();


        Double newBalance = account.getBalance() + transactionRequestDTO.getAmount();
        account.setBalance(newBalance);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.DEPOSIT)
                .amount(transactionRequestDTO.getAmount())
                .account(account)
                .transactionDate(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        accountRepository.save(account);

        return TransactionResponseDTO.builder()
                .transactionId(savedTransaction.getId())
                .accountNumber(account.getAccountNumber())
                .balance(newBalance)
                .transactionType(TransactionType.DEPOSIT)
                .transactionDate(savedTransaction.getTransactionDate())
                .build();
    }

    public List<TransactionResponseDTO> getAllTransactionsByUserId(UUID userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        List<Transaction> transactions = transactionRepository.findByAccountOrderByTransactionDateDesc(account);

        return transactions.stream()
                .map(transaction -> TransactionResponseDTO.builder()
                        .transactionId(transaction.getId())
                        .accountNumber(transaction.getAccount().getAccountNumber())
                        .amount(transaction.getAmount())
                        .balance(transaction.getAccount().getBalance())
                        .transactionType(transaction.getTransactionType())
                        .transactionDate(transaction.getTransactionDate())
                        .build())
                .collect(Collectors.toList());
    }
}


