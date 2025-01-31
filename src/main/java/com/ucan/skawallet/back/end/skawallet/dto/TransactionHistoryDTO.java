/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author azm
 */
@Data
@AllArgsConstructor
public class TransactionHistoryDTO
{

    private Long transactionId;           // Transaction ID
    private String transactionType;       // Tipo de transação
    private BigDecimal amount;            // Valor da transação
    private String status;                // Status da transação
    private LocalDateTime timestamp;      // Timestamp da transação
}
