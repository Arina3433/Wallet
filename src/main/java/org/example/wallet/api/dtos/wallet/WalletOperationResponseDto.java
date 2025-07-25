package org.example.wallet.api.dtos.wallet;

import java.math.BigDecimal;

public record WalletOperationResponseDto
        (
                String description,
                BigDecimal amount
        ) {
}
