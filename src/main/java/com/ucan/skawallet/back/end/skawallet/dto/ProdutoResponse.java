/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author azm
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProdutoResponse
{

    private Long id;
    private String nome;
    private BigDecimal preco;
    private Long partnerId;
    private String partnerName;
}
