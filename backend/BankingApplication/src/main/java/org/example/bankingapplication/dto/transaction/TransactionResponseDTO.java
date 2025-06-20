package org.example.bankingapplication.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bankingapplication.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {

    private UUID transactionId;
    private String senderAccountNumber;
    private String recipientAccountNumber;
    private BigDecimal amount;
    private BigDecimal balance;
    private TransactionType transactionType;
    private LocalDateTime transactionDate;
    private String message;

    public TransactionResponseDTO(String message) {
        this.message = message;
    }
}
