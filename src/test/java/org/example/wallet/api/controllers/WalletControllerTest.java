package org.example.wallet.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.wallet.api.dtos.wallet.OperationType;
import org.example.wallet.api.dtos.wallet.WalletBalanceDto;
import org.example.wallet.api.dtos.wallet.WalletOperationResponseDto;
import org.example.wallet.api.dtos.wallet.WalletRequestDto;
import org.example.wallet.api.errors.ServerErrorCode;
import org.example.wallet.api.errors.ServerException;
import org.example.wallet.api.services.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WalletController.class)
@ExtendWith(SpringExtension.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final UUID WALLET_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @TestConfiguration
    static class MockConfig {
        @Bean
        public WalletService walletService() {
            return mock(WalletService.class);
        }
    }

    @Test
    void doOperationDeposit_success() throws Exception {
        WalletRequestDto requestDto =
                new WalletRequestDto(WALLET_ID, OperationType.DEPOSIT, new BigDecimal("100.00"));
        WalletOperationResponseDto responseDto =
                new WalletOperationResponseDto("Пополнение", new BigDecimal("100.00"));

        when(walletService.doOperation(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Пополнение"))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void doOperation_withdrawSuccess() throws Exception {
        WalletRequestDto requestDto =
                new WalletRequestDto(WALLET_ID, OperationType.WITHDRAW, new BigDecimal("50.00"));
        WalletOperationResponseDto responseDto =
                new WalletOperationResponseDto("Снятие", new BigDecimal("50.00"));

        when(walletService.doOperation(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Снятие"))
                .andExpect(jsonPath("$.amount").value(50.00));
    }


    @Test
    void getBalance_success() throws Exception {
        WalletBalanceDto balanceDto = new WalletBalanceDto(new BigDecimal("123.45"));

        when(walletService.getBalance(WALLET_ID)).thenReturn(balanceDto);

        mockMvc.perform(get("/api/v1/wallet/{wallet_uuid}", WALLET_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(123.45));
    }

    @Test
    void doOperation_insufficientFunds_shouldReturnBadRequest() throws Exception {
        WalletRequestDto requestDto = new WalletRequestDto(WALLET_ID, OperationType.WITHDRAW, new BigDecimal("100.00"));

        when(walletService.doOperation(requestDto))
                .thenThrow(new ServerException(ServerErrorCode.INSUFFICIENT_FUNDS));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorCode").value("INSUFFICIENT_FUNDS"))
                .andExpect(jsonPath("$.errors[0].message").value(
                        "На вашем счете не достаточно средств для выполнения операции."
                ));
    }

    @Test
    void getBalance_walletNotFound_shouldReturnBadRequest() throws Exception {
        UUID nonExistingId = UUID.fromString("11111111-1111-1111-1111-111111111110");

        when(walletService.getBalance(nonExistingId))
                .thenThrow(new ServerException(ServerErrorCode.WALLET_NOT_FOUND, WALLET_ID.toString()));

        mockMvc.perform(get("/api/v1/wallet/{wallet_uuid}", nonExistingId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorCode").value("WALLET_NOT_FOUND"))
                .andExpect(jsonPath("$.errors[0].message").value(
                        "Кошелек с UUID 11111111-1111-1111-1111-111111111111 не найден."));
    }

    @ParameterizedTest
    @MethodSource("invalidRequestProvider")
    void doOperation_validationErrors(WalletRequestDto invalidDto, String expectedMessage) throws Exception {
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*].errorCode").value(hasItem("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.errors[*].message").value(hasItem(expectedMessage)));
    }

    @Test
    void getBalance_invalidUuid_shouldReturnBadRequest() throws Exception {
        String invalidUuid = "not-a-uuid";

        mockMvc.perform(get("/api/v1/wallet/{wallet_uuid}", invalidUuid))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertAll(
                        () -> assertTrue(result.getResponse().getContentAsString().contains("INVALID_TYPE")),
                        () -> assertTrue(result.getResponse().getContentAsString().contains(
                                "Неверный тип параметра: wallet_uuid. Ожидается UUID"))
                ));
    }

    @Test
    void methodNotAllowed_shouldReturnMethodNotAllowedError() throws Exception {
        mockMvc.perform(get("/api/v1/wallet"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.errors[0].errorCode").value(
                        "METHOD_NOT_ALLOWED"))
                .andExpect(jsonPath("$.errors[0].message").value(
                        "Запрос с методом GET не поддерживается на данный адрес"));
    }

    @Test
    void noHandlerFound_shouldReturnNotFoundError() throws Exception {
        mockMvc.perform(get("/api/v1/non-existent-url"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errors[0].message").value(
                        "Ресурс по адресу /api/v1/non-existent-url не найден"));
    }

    public static Stream<Arguments> invalidRequestProvider() {
        return Stream.of(
                Arguments.of(
                        new WalletRequestDto(null, OperationType.DEPOSIT, new BigDecimal("100.00")),
                        "Поле \"id кошелька\" не может быть пустым"
                ),
                Arguments.of(
                        new WalletRequestDto(WALLET_ID, null, new BigDecimal("100.00")),
                        "Нужно выбрать тип операции"
                ),
                Arguments.of(
                        new WalletRequestDto(WALLET_ID, OperationType.DEPOSIT, null),
                        "Поле \"сумма\" не может быть пустым"
                ),
                Arguments.of(
                        new WalletRequestDto(WALLET_ID, OperationType.DEPOSIT, new BigDecimal("-10")),
                        "Сумма операции должна быть больше 0"
                )
        );
    }
}
