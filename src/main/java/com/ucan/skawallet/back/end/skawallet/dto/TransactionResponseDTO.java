/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.dto;

import com.ucan.skawallet.back.end.skawallet.enums.PaymentMethod;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionStatus;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDTO
{

    private Long transactionId;         // ID da transação
    private BigDecimal amount;          // Valor da transação
    private TransactionType transactionType = TransactionType.PAYMENT; // Tipo de transação (DEPOSIT, WITHDRAWAL, etc.)
    private TransactionStatus status = TransactionStatus.COMPLETED;   // Status da transação (PENDING, COMPLETED, FAILED)
    private LocalDateTime createdAt;    // Data de criação da transação
    private LocalDateTime completedAt;  // Data de conclusão da transação
    private String sourceWalletName;    // Nome da carteira de origem
    private String destinationWalletName; // Nome da carteira de destino
    private PaymentMethod paymentMethod = PaymentMethod.DIGITAL_WALLET; // Método de pagamento
    private String description;         // Descrição da transação
//      private String userName;
}
