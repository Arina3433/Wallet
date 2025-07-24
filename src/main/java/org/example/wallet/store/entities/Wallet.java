package org.example.wallet.store.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallet")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    @Id
    @Column(name = "wallet_id")
    private UUID walletId;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;
}
