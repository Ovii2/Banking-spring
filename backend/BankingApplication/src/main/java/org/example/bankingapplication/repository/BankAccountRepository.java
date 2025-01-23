package org.example.bankingapplication.repository;

import org.example.bankingapplication.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<Account, UUID> {
}
