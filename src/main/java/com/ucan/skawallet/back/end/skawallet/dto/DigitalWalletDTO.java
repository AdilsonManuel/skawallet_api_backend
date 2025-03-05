/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.dto;

import com.ucan.skawallet.back.end.skawallet.enums.WalletType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DigitalWalletDTO
{

    private String walletName;
    private WalletType walletType; // Enum: PERSONAL, MERCHANT, SAVINGS
    private BigDecimal balance;
    private String currency;
    private Boolean isDefault;
    private Long userId; // ID do usuário associado à carteira
    private Long pk_digital_wallets;

}
