/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.dto;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author azm
 */
@Data
public class ProdutoDTO
{

    private Long id;
    private String nome;
    private BigDecimal preco;
    private String descricao;

    // NOTA: Não incluímos o campo 'partner' aqui para evitar o ciclo de recursão.
}
