package org.example.bankingapplication.repository;

import org.example.bankingapplication.model.Account;
import org.example.bankingapplication.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByAccountOrderByTransactionDateDesc(Account account);
}
