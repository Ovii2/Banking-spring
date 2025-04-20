package org.example.bankingapplication.repository;

import org.example.bankingapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByUsername(String username);

    boolean existsUserByEmail(String email);

    boolean existsUserByUsername(String username);

    boolean existsByAccountNumber(String accountNumber);
}
