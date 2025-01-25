package org.example.bankingapplication.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bankingapplication.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {

    private UUID transactionId;
    private String accountNumber;
    private Double amount;
    private Double balance;
    private TransactionType transactionType;
    private LocalDateTime transactionDate;

}
