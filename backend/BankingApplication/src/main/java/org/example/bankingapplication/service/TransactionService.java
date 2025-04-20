package org.example.bankingapplication.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bankingapplication.dto.transaction.TransactionRequestDTO;
import org.example.bankingapplication.dto.transaction.TransactionResponseDTO;
import org.example.bankingapplication.enums.TransactionType;
import org.example.bankingapplication.exceptions.AccountNotFoundException;
import org.example.bankingapplication.model.Account;
import org.example.bankingapplication.model.Transaction;
import org.example.bankingapplication.repository.AccountRepository;
import org.example.bankingapplication.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    @Transactional
    public TransactionResponseDTO deposit(TransactionRequestDTO transactionRequestDTO) {
        checkAmount(transactionRequestDTO.getAmount());

        Account account = accountRepository.findByAccountNumber(transactionRequestDTO.getSenderAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        BigDecimal newBalance = account.getBalance().add(transactionRequestDTO.getAmount());
        account.setBalance(newBalance);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.DEPOSIT)
                .amount(transactionRequestDTO.getAmount())
                .accountNumber(account.getAccountNumber())
                .transactionDate(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        accountRepository.save(account);

        return TransactionResponseDTO.builder()
                .transactionId(savedTransaction.getId())
                .senderAccountNumber(account.getAccountNumber())
                .balance(newBalance)
                .transactionType(TransactionType.DEPOSIT)
                .transactionDate(savedTransaction.getTransactionDate())
                .message("Deposit successful")
                .build();

    }

    @Transactional
    public TransactionResponseDTO withdraw(TransactionRequestDTO transactionRequestDTO) {
        checkAmount(transactionRequestDTO.getAmount());

        Account account = accountRepository.findByAccountNumber(transactionRequestDTO.getSenderAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (account.getBalance().compareTo(transactionRequestDTO.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        BigDecimal newBalance = account.getBalance().subtract(transactionRequestDTO.getAmount());
        account.setBalance(newBalance);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.WITHDRAW)
                .amount(transactionRequestDTO.getAmount())
                .accountNumber(account.getAccountNumber())
                .transactionDate(LocalDateTime.now())
                .build();
        Transaction savedTransaction = transactionRepository.save(transaction);

        accountRepository.save(account);

        return TransactionResponseDTO.builder()
                .transactionId(savedTransaction.getId())
                .senderAccountNumber(account.getAccountNumber())
                .balance(newBalance)
                .transactionType(TransactionType.WITHDRAW)
                .transactionDate(savedTransaction.getTransactionDate())
                .message("Withdrawal successful")
                .build();
    }


    @Transactional
    public TransactionResponseDTO transfer(TransactionRequestDTO transactionRequestDTO) {
        if (transactionRequestDTO.getRecipientAccountNumber() == null) {
            throw new IllegalArgumentException("Receiver account number is required for transfers.");
        }

        checkAmount(transactionRequestDTO.getAmount());

        Account senderAccount = accountRepository.findByAccountNumber(transactionRequestDTO.getSenderAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found"));

        Account receiverAccount = accountRepository.findByAccountNumber(transactionRequestDTO.getRecipientAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Receiver account not found"));

        if (senderAccount.getBalance().compareTo(transactionRequestDTO.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        senderAccount.setBalance(senderAccount.getBalance().subtract(transactionRequestDTO.getAmount()));
        receiverAccount.setBalance(receiverAccount.getBalance().add(transactionRequestDTO.getAmount()));

        Transaction senderTransaction = Transaction.builder()
                .transactionType(TransactionType.TRANSFER_OUT)
                .amount(transactionRequestDTO.getAmount())
                .accountNumber(senderAccount.getAccountNumber())
                .receiverAccountNumber(receiverAccount.getAccountNumber())
                .transactionDate(LocalDateTime.now())
                .build();

        Transaction receiverTransaction = Transaction.builder()
                .transactionType(TransactionType.TRANSFER_IN)
                .amount(transactionRequestDTO.getAmount())
                .accountNumber(receiverAccount.getAccountNumber())
                .receiverAccountNumber(senderAccount.getAccountNumber())
                .transactionDate(LocalDateTime.now())
                .build();

        transactionRepository.save(senderTransaction);
        transactionRepository.save(receiverTransaction);

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        return TransactionResponseDTO.builder()
                .transactionId(senderTransaction.getId())
                .senderAccountNumber(senderAccount.getAccountNumber())
                .recipientAccountNumber(receiverAccount.getAccountNumber())
                .balance(senderAccount.getBalance())
                .transactionType(TransactionType.TRANSFER_OUT)
                .transactionDate(senderTransaction.getTransactionDate())
                .message("Funds transferred successfully")
                .build();
    }


    @Transactional
    public List<TransactionResponseDTO> getAllTransactionsByUserId(UUID userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        List<Transaction> transactions = transactionRepository.findByAccountNumberOrderByTransactionDateDesc(account.getAccountNumber());

        return transactions.stream()
                .map(transaction -> TransactionResponseDTO.builder()
                        .transactionId(transaction.getId())
                        .senderAccountNumber(transaction.getAccountNumber())
                        .recipientAccountNumber(transaction.getReceiverAccountNumber())
                        .amount(transaction.getAmount())
                        .balance(account.getBalance())
                        .transactionType(transaction.getTransactionType())
                        .transactionDate(transaction.getTransactionDate())
                        .build())
                .collect(Collectors.toList());

    }

    public void checkAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }
}


