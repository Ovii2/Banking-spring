package org.example.bankingapplication.dto.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bankingapplication.enums.TransactionType;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionRequestDTO {

//    @NotBlank(message = "Account number is required")
    private String accountNumber;

//    @NotBlank(message = "Recipient account number is required")
    private String recipientAccountNumber;

//    @NotNull(message = "Amount is required")
//    @Positive(message = "Amount must be positive")
    private Double amount;

    private TransactionType transactionType;
}
