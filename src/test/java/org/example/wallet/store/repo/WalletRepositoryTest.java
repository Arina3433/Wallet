package org.example.wallet.store.repo;

import org.example.wallet.store.entities.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class WalletRepositoryTest {
    @Autowired
    private WalletRepository walletRepository;

    private static final UUID WALLET_ID_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID WALLET_ID_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Test
    void testFindById() {
        Wallet wallet = walletRepository.findById(WALLET_ID_1).orElseThrow();
        assertEquals(new BigDecimal("1000.00"), wallet.getBalance());
    }

    @Test
    void testFindById_NotFound() {
        UUID nonExistentId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        assertTrue(walletRepository.findById(nonExistentId).isEmpty());
    }

    @Test
    void testWithdrawSuccess() {
        BigDecimal amount = new BigDecimal("200.00");
        int updated = walletRepository.withdraw(WALLET_ID_1, amount);
        Wallet wallet = walletRepository.findById(WALLET_ID_1).orElseThrow();

        assertAll(
                () -> assertEquals(1, updated),
                () -> assertEquals(new BigDecimal("800.00"), wallet.getBalance())
        );
    }

    @Test
    void testWithdrawFailDueToInsufficientFunds() {
        BigDecimal amount = new BigDecimal("600.00");
        int updated = walletRepository.withdraw(WALLET_ID_2, amount);
        Wallet wallet = walletRepository.findById(WALLET_ID_2).orElseThrow();

        assertAll(
                () -> assertEquals(0, updated),
                () -> assertEquals(new BigDecimal("500.00"), wallet.getBalance())
        );
    }

    @Test
    void testDeposit() {
        BigDecimal amount = new BigDecimal("150.00");
        int updated = walletRepository.deposit(WALLET_ID_2, amount);
        Wallet wallet = walletRepository.findById(WALLET_ID_2).orElseThrow();

        assertAll(
                () -> assertEquals(1, updated),
                () -> assertEquals(new BigDecimal("650.00"), wallet.getBalance())
        );
    }
}