package org.example.wallet.api.services;

import org.example.wallet.api.dtos.wallet.OperationType;
import org.example.wallet.api.dtos.wallet.WalletBalanceDto;
import org.example.wallet.api.dtos.wallet.WalletOperationResponseDto;
import org.example.wallet.api.dtos.wallet.WalletRequestDto;
import org.example.wallet.api.errors.ServerErrorCode;
import org.example.wallet.api.errors.ServerException;
import org.example.wallet.api.mappers.WalletMapper;
import org.example.wallet.store.entities.Wallet;
import org.example.wallet.store.repo.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {
    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletMapper walletMapper;

    @InjectMocks
    private WalletService walletService;

    private final UUID WALLET_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private WalletRequestDto withdrawDto;
    private WalletRequestDto depositDto;
    private Wallet wallet;

    @BeforeEach
    void setup() {
        withdrawDto = new WalletRequestDto(WALLET_ID, OperationType.WITHDRAW, new BigDecimal("100.00"));
        depositDto = new WalletRequestDto(WALLET_ID, OperationType.DEPOSIT, new BigDecimal("200.00"));
        wallet = new Wallet(WALLET_ID, new BigDecimal("500.00"));
    }

    @Test
    void doOperation_withdraw_success() {
        WalletOperationResponseDto responseDto =
                new WalletOperationResponseDto("Списание", withdrawDto.amount());

        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(walletRepository.withdraw(WALLET_ID, withdrawDto.amount())).thenReturn(1);
        when(walletMapper.toOperationResponseDto(withdrawDto))
                .thenReturn(responseDto);

        WalletOperationResponseDto result = walletService.doOperation(withdrawDto);

        assertAll("Withdraw operation",
                () -> assertNotNull(result),
                () -> assertEquals("Списание", result.description()),
                () -> assertEquals(0, result.amount().compareTo(new BigDecimal("100.00")))
        );

        verify(walletRepository).findById(WALLET_ID);
        verify(walletRepository).withdraw(WALLET_ID, withdrawDto.amount());
        verify(walletMapper).toOperationResponseDto(withdrawDto);
    }

    @Test
    void doOperation_deposit_success() {
        WalletOperationResponseDto responseDto =
                new WalletOperationResponseDto("Пополнение", depositDto.amount());

        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(walletRepository.deposit(WALLET_ID, depositDto.amount())).thenReturn(1);
        when(walletMapper.toOperationResponseDto(depositDto))
                .thenReturn(responseDto);

        WalletOperationResponseDto result = walletService.doOperation(depositDto);

        assertAll("Deposit operation",
                () -> assertNotNull(result),
                () -> assertEquals("Пополнение", result.description()),
                () -> assertEquals(0, result.amount().compareTo(new BigDecimal("200.00")))
        );

        verify(walletRepository).findById(WALLET_ID);
        verify(walletRepository).deposit(WALLET_ID, depositDto.amount());
        verify(walletMapper).toOperationResponseDto(depositDto);
    }

    @Test
    void doOperation_walletNotFound_throws() {
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.empty());

        ServerException ex = assertThrows(ServerException.class, () -> walletService.doOperation(withdrawDto));

        assertAll("Wallet not found exception",
                () -> assertEquals(ServerErrorCode.WALLET_NOT_FOUND, ex.getServerErrorCode()),
                () -> verify(walletRepository).findById(WALLET_ID),
                () -> verify(walletRepository, never()).withdraw(any(), any()),
                () -> verify(walletRepository, never()).deposit(any(), any())
        );
    }

    @Test
    void doOperation_insufficientFunds_throws() {
        Wallet lowBalanceWallet = new Wallet(WALLET_ID, new BigDecimal("50"));

        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(lowBalanceWallet));
        when(walletRepository.withdraw(WALLET_ID, withdrawDto.amount())).thenReturn(0);

        ServerException ex = assertThrows(ServerException.class, () -> walletService.doOperation(withdrawDto));

        assertAll("Insufficient funds exception",
                () -> assertEquals(ServerErrorCode.INSUFFICIENT_FUNDS, ex.getServerErrorCode()),
                () -> verify(walletRepository).findById(WALLET_ID),
                () -> verify(walletRepository).withdraw(WALLET_ID, withdrawDto.amount())
        );
    }

    @Test
    void getBalance_success() {
        WalletBalanceDto balanceDto = new WalletBalanceDto(new BigDecimal("500.00"));

        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(walletMapper.toBalanceDto(wallet)).thenReturn(balanceDto);

        WalletBalanceDto result = walletService.getBalance(WALLET_ID);

        assertAll("Get balance",
                () -> assertNotNull(result),
                () -> assertEquals(0, result.balance().compareTo(new BigDecimal("500.00"))),
                () -> verify(walletRepository).findById(WALLET_ID),
                () -> verify(walletMapper).toBalanceDto(wallet)
        );
    }

    @Test
    void getBalance_walletNotFound_throws() {
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.empty());

        ServerException ex = assertThrows(ServerException.class, () -> walletService.getBalance(WALLET_ID));

        assertAll("Wallet not found on balance request",
                () -> assertEquals(ServerErrorCode.WALLET_NOT_FOUND, ex.getServerErrorCode()),
                () -> verify(walletRepository).findById(WALLET_ID),
                () -> verify(walletMapper, never()).toBalanceDto(any())
        );
    }
}
