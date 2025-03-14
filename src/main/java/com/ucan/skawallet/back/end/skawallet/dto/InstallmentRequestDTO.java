/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class InstallmentRequestDTO
{

    private Long userId;
    private Long partnerId;
    private BigDecimal amount;
    private Integer installments;
    private String description;
}
