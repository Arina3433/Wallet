package org.example.wallet.store.repo;

import org.example.wallet.store.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @Modifying
    @Query(value = """
                UPDATE wallet
                SET balance = balance - :amount
                WHERE wallet_id = :walletId AND balance >= :amount
            """, nativeQuery = true)
    int withdraw(@Param("walletId") UUID walletId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query(value = """
                UPDATE wallet
                SET balance = balance + :amount
                WHERE wallet_id = :walletId
            """, nativeQuery = true)
    int deposit(@Param("walletId") UUID walletId, @Param("amount") BigDecimal amount);
}
