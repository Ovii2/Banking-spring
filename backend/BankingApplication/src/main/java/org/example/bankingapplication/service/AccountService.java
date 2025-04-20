package org.example.bankingapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.bankingapplication.dto.account.AccountRequestDTO;
import org.example.bankingapplication.dto.account.AccountResponseDTO;
import org.example.bankingapplication.exceptions.AccountNotFoundException;
import org.example.bankingapplication.model.Account;
import org.example.bankingapplication.model.User;
import org.example.bankingapplication.repository.AccountRepository;
import org.example.bankingapplication.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO) {
        if (accountRequestDTO.getUserId() == null || accountRequestDTO.getAccountNumber() == null) {
            throw new IllegalArgumentException("User ID and Account Number cannot be null");
        }

        User user = userRepository.findById(accountRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getAccounts() == null) {
            user.setAccounts(new HashSet<>());
        }

        Account account = Account.builder()
                .accountNumber(accountRequestDTO.getAccountNumber())
                .ownerName(user.getUsername())
                .balance(accountRequestDTO.getInitialDeposit() != null ? accountRequestDTO.getInitialDeposit() : BigDecimal.ZERO)
                .user(user)
                .build();

        Account savedAccount = accountRepository.save(account);
        user.getAccounts().add(savedAccount);
        userRepository.save(user);

        return AccountResponseDTO.builder()
                .accountId(savedAccount.getId())
                .accountNumber(savedAccount.getAccountNumber())
                .ownerName(account.getOwnerName())
                .balance(savedAccount.getBalance())
                .build();
    }

    public AccountResponseDTO getAccountByUserId(UUID userId) {
        return accountRepository.findByUserId(userId)
                .map(account -> AccountResponseDTO.builder()
                        .accountId(account.getId())
                        .accountNumber(account.getAccountNumber())
                        .ownerName(account.getOwnerName())
                        .balance(account.getBalance())
                        .build())
                .orElseThrow(() -> new AccountNotFoundException("Account not found for user: " + userId));
    }

    public static String generateAccountNumber() {
        String countryCode = "LT";
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder(countryCode);
        accountNumber.append(random.nextInt(90) + 10);
        IntStream.range(0, 16).
                forEach(i -> accountNumber.append(random.nextInt(10)));

        return accountNumber.toString();
    }
}
