package org.example.bankingapplication.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Transaction> transactions;

    @PrePersist
    public void onCreate() {
        if (this.balance == null) {
            this.balance = 0.0;
        }
    }
}
