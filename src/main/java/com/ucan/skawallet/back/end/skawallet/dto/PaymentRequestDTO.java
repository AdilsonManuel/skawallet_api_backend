package com.ucan.skawallet.back.end.skawallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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