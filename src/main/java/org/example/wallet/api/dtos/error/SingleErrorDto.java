package org.example.wallet.api.dtos.error;

import lombok.Builder;

@Builder
public record SingleErrorDto(
        String errorCode,
        String message) {
}
