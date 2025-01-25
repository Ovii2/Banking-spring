package org.example.bankingapplication.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "balance")
    private Double balance;

    @ManyToMany(mappedBy = "accounts")
    private Set<User> users;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> transactions;

    @PrePersist
    public void onCreate() {
        if (this.balance == null) {
            this.balance = 0.0;
        }
    }
}
