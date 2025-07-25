package org.example.wallet.api.services;

import lombok.RequiredArgsConstructor;
import org.example.wallet.api.dtos.wallet.OperationType;
import org.example.wallet.api.dtos.wallet.WalletOperationResponseDto;
import org.example.wallet.api.dtos.wallet.WalletRequestDto;
import org.example.wallet.api.dtos.wallet.WalletBalanceDto;
import org.example.wallet.api.errors.ServerErrorCode;
import org.example.wallet.api.errors.ServerException;
import org.example.wallet.store.repo.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;

    @Transactional
    public WalletOperationResponseDto doOperation(WalletRequestDto dto) {
        walletRepository.findById(dto.walletId()).orElseThrow(() ->
                new ServerException(ServerErrorCode.WALLET_NOT_FOUND, dto.walletId().toString()));

        int updatedRows = 0;
        if (dto.operationType().equals(OperationType.WITHDRAW)) {
            updatedRows = walletRepository.withdraw(dto.walletId(), dto.amount());
        }
        if (dto.operationType().equals(OperationType.DEPOSIT)) {
            updatedRows = walletRepository.deposit(dto.walletId(), dto.amount());
        }

        if (updatedRows == 0) {
            throw new ServerException(ServerErrorCode.INSUFFICIENT_FUNDS);
        }

        return null;
    }


    public WalletBalanceDto getBalance(String walletUuid) {
        return null;
    }
}
