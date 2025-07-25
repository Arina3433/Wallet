package org.example.wallet.api.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServerErrorCode {
    WALLET_NOT_FOUND("Кошелек с UUID %s не найден."),
    INSUFFICIENT_FUNDS("На вашем счете не достаточно средств для выполнения операции.");

    private final String errorMessage;
}
