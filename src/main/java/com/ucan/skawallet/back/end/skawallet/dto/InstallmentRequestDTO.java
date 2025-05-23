/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.dto;

import lombok.Data;

@Data
public class InstallmentRequestDTO
{

    private String idDocument;
    private Long partnerId;
    private Integer installments;
    private String description;
    private String WalletCode;
    private Long productId; // Novo campo
}
