/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
@Builder
public class InstallmentHistoryDTO
{

    private Long installmentId;  // ID da parcela
    private String partnerName;
    private String productName;
    private BigDecimal productPrice;  // Corrigido para camelCase
    private BigDecimal totalAmount;
    private int installments;
    private int remainingInstallments;
    private BigDecimal monthlyPayment;
    private LocalDate nextDueDate;
    private String status;
}
