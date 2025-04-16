/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.model;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class TopUpRequest
{

    private String walletCode;
    private BigDecimal amount;
}
