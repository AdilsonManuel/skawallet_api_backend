/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.dto;

import com.ucan.skawallet.back.end.skawallet.enums.PaymentMethod;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionStatus;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO
{

    private BigDecimal amount; // Valor da transação
    private TransactionType transactionType; // Tipo de transação (DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT)
    private PaymentMethod paymentMethod; // Método de pagamento (CARD, BANK, DIGITAL_WALLET)
    private String description; // Descrição opcional
    private Long sourceWalletId; // ID da carteira de origem (opcional)
    private Long destinationWalletId; // ID da carteira de destino (opcional)
    private TransactionStatus status;
}
