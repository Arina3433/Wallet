package org.example.wallet.api.dtos.wallet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OperationType {
    DEPOSIT("Пополнение"),
    WITHDRAW("Снятие");

    private final String value;
}
