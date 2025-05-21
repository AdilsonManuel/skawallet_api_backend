/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.model.Produto;
import com.ucan.skawallet.back.end.skawallet.repository.ProdutoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author azm
 */
// ProdutoService.java
@Service
public class ProdutoService
{

    @Autowired
    private ProdutoRepository produtoRepository;

    public Produto salvarProduto (Produto produto)
    {
        return produtoRepository.save(produto);
    }

    public List<Produto> listarProdutos ()
    {
        return produtoRepository.findAll();
    }

    public Produto buscarPorId (Long id)
    {
        return produtoRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public void deletar (Long id)
    {
        produtoRepository.deleteById(id);
    }

    public List<Produto> listarPorParceiro (Long partnerId)
    {
        return produtoRepository.findByPartnerId(partnerId);
    }
}
