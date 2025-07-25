package org.example.wallet.api.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.wallet.api.dtos.wallet.WalletOperationResponseDto;
import org.example.wallet.api.dtos.wallet.WalletRequestDto;
import org.example.wallet.api.dtos.wallet.WalletBalanceDto;
import org.example.wallet.api.services.WalletService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/wallet")
public class WalletController {
    private final WalletService walletService;

    private static final String GET_BALANCE = "/{wallet_uuid}";

    @PostMapping
    public WalletOperationResponseDto doOperation(@RequestBody @Valid WalletRequestDto walletRequestDto) {
        return walletService.doOperation(walletRequestDto);
    }

    @GetMapping(GET_BALANCE)
    public WalletBalanceDto getBalance(@PathVariable("wallet_uuid") String walletUuid) {
        return walletService.getBalance(walletUuid);
    }

}
