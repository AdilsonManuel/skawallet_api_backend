/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class InstallmentHistoryDTO
{

    private String partnerName;
    private BigDecimal totalAmount;
    private int installments;
    private int remainingInstallments;
    private BigDecimal monthlyPayment;
    private LocalDate nextDueDate;
    private String status;
}
