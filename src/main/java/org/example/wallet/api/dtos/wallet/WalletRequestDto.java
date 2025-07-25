package org.example.wallet.api.dtos.wallet;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletRequestDto(
        @NotNull(message = "Поле \"id кошелька\" не может быть пустым")
        UUID walletId,

        @NotNull(message = "Нужно выбрать тип операции")
        OperationType operationType,

        @NotNull(message = "Поле \"сумма\" не может быть пустым")
        @Positive(message = "Сумма операции должна быть больше 0")
        BigDecimal amount
) {
}
