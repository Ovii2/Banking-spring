package org.example.bankingapplication.dto.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponseDTO {

    private UUID accountId;
    private String accountNumber;
    private String ownerName;
    private Double balance;
    private String message;

    public AccountResponseDTO(String message) {
        this.message = message;
    }
}
