package org.example.wallet.api.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.wallet.api.dtos.wallet.OperationType;
import org.example.wallet.api.dtos.wallet.WalletOperationResponseDto;
import org.example.wallet.api.dtos.wallet.WalletRequestDto;
import org.example.wallet.api.dtos.wallet.WalletBalanceDto;
import org.example.wallet.api.errors.ServerErrorCode;
import org.example.wallet.api.errors.ServerException;
import org.example.wallet.api.mappers.WalletMapper;
import org.example.wallet.store.entities.Wallet;
import org.example.wallet.store.repo.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    @Transactional
    public WalletOperationResponseDto doOperation(WalletRequestDto dto) {
        UUID walletId = dto.walletId();
        OperationType type = dto.operationType();

        log.info("Attempting {} operation for wallet {}", type, walletId);

        walletRepository.findById(dto.walletId()).orElseThrow(() ->
                new ServerException(ServerErrorCode.WALLET_NOT_FOUND, walletId.toString()));

        int updatedRows = 0;
        if (type.equals(OperationType.WITHDRAW)) {
            updatedRows = walletRepository.withdraw(walletId, dto.amount());
        }
        if (type.equals(OperationType.DEPOSIT)) {
            updatedRows = walletRepository.deposit(walletId, dto.amount());
        }

        if (updatedRows == 0) {
            throw new ServerException(ServerErrorCode.INSUFFICIENT_FUNDS);
        }

        log.info("{} operation successful for wallet {}", type, walletId);
        return walletMapper.toOperationResponseDto(dto);
    }

    @Transactional(readOnly = true)
    public WalletBalanceDto getBalance(UUID walletUuid) {
        log.info("Balance request for wallet {}", walletUuid);

        Wallet wallet = walletRepository.findById(walletUuid)
                .orElseThrow(() -> new ServerException(ServerErrorCode.WALLET_NOT_FOUND, walletUuid.toString()));

        log.info("Wallet {} found, returning balance", walletUuid);
        return walletMapper.toBalanceDto(wallet);
    }
}
