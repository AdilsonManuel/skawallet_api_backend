/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.dto.ProdutoResponse;
import com.ucan.skawallet.back.end.skawallet.model.Produto;
import org.springframework.stereotype.Component;

/**
 *
 * @author azm
 */
@Component
public class ProdutoMapper
{

    public ProdutoResponse toResponse (Produto produto)
    {
        if (produto == null)
        {
            return null;
        }

        return ProdutoResponse.builder()
                .id(produto.getId())
                .nome(produto.getNome())
                .preco(produto.getPreco())
                .partnerId(produto.getPartner() != null ? produto.getPartner().getPkPartners() : null)
                .partnerName(produto.getPartner() != null ? produto.getPartner().getName() : null)
                .build();
    }
}
