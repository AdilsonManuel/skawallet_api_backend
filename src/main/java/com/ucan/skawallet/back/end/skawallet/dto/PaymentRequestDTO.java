package com.ucan.skawallet.back.end.skawallet.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO
{

    private Long sourceWalletId;         // ID da carteira de origem
    private Long destinationWalletId;    // ID da carteira de destino
    private BigDecimal amount;           // Valor da transação
    private String description;          // Descrição opcional do pagamento
    private String paymentMethod;        // Método de pagamento (e.g., "DIGITAL_WALLET", "CARD", "BANK")
}
