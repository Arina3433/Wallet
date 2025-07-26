package org.example.wallet.api.mappers;

import org.example.wallet.api.dtos.wallet.WalletBalanceDto;
import org.example.wallet.api.dtos.wallet.WalletOperationResponseDto;
import org.example.wallet.api.dtos.wallet.WalletRequestDto;
import org.example.wallet.store.entities.Wallet;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    default WalletOperationResponseDto toOperationResponseDto(WalletRequestDto dto) {
        String description = dto.operationType().getValue();
        BigDecimal amount = dto.amount().setScale(2, RoundingMode.HALF_UP);

        return new WalletOperationResponseDto(description, amount);
    }

    WalletBalanceDto toBalanceDto(Wallet wallet);
}

