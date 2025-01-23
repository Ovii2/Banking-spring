package org.example.bankingapplication.repository;

import org.example.bankingapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserById(UUID uuid);

    boolean existsUserByEmail(String email);

    boolean existsUserByUsername(String username);
}
