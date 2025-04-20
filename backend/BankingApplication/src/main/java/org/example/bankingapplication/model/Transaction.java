package org.example.bankingapplication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bankingapplication.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @JoinColumn(name = "account_number", nullable = false)
    private String accountNumber;

    @JoinColumn(name = "receiver_account_number", nullable = false)
    private String receiverAccountNumber;

    @PrePersist
    public void prePersist() {
        this.transactionDate = ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime();
    }
}
