/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author azm
 */
@Data
public class DepositRequestDTO
{

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor mínimo permitido é 0.01")
    private BigDecimal amount; // Valor a ser carregado na carteira

    @NotBlank(message = "O número do cartão é obrigatório")
    @Pattern(regexp = "\\d{16}", message = "Número do cartão inválido. Deve conter 16 dígitos")
    private String cardNumber; // Número do cartão (VISA ou Multicaixa)

    @NotBlank(message = "A data de validade é obrigatória")
    @Pattern(regexp = "(0[1-9]|1[0-2])\\/(\\d{2})", message = "Formato inválido. Use MM/YY")
    private String expiryDate; // Data de validade no formato MM/YY

    @NotBlank(message = "O CVV é obrigatório")
    @Pattern(regexp = "\\d{3}", message = "O CVV deve conter exatamente 3 dígitos")
    private String cvv; // Código de segurança do cartão

    @NotBlank(message = "O código da carteira é obrigatório")
    private String walletCode; // Código da carteira onde o valor será depositado
}
