package org.example.wallet.api.mappers;

import org.example.wallet.api.dtos.wallet.WalletBalanceDto;
import org.example.wallet.api.dtos.wallet.WalletOperationResponseDto;
import org.example.wallet.api.dtos.wallet.WalletRequestDto;
import org.example.wallet.store.entities.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    @Mapping(target = "description", expression = "java(dto.operationType().getValue")
    @Mapping(target = "amount", expression = "java(dto.amount().setScale(2, java.math.RoundingMode.HALF_UP))")
    WalletOperationResponseDto toOperationResponseDto(WalletRequestDto dto);

    WalletBalanceDto toBalanceDto(Wallet wallet);
}
