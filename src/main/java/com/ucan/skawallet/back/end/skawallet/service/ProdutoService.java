/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.dto.ProdutoResponse;
import com.ucan.skawallet.back.end.skawallet.model.Produto;
import com.ucan.skawallet.back.end.skawallet.repository.ProdutoRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author azm
 */
// ProdutoService.java
@Service
@RequiredArgsConstructor
public class ProdutoService
{

    // @Autowired é redundante com @RequiredArgsConstructor, mas ok
    private final ProdutoRepository produtoRepository;

    private final ProdutoMapper produtoMapper;

    public Produto salvarProduto (Produto produto)
    {
        return produtoRepository.save(produto);
    }

    // CORRIGIDO: Retorna List<ProdutoResponse> para quebrar o ciclo de recursão
    public List<ProdutoResponse> listarProdutos ()
    {
        return produtoRepository.findAll().stream()
                .map(produtoMapper::toResponse) // Usa o mapper
                .collect(Collectors.toList());
    }

    public Produto buscarPorId (Long id)
    {
        return produtoRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public void deletar (Long id)
    {
        produtoRepository.deleteById(id);
    }

    public List<ProdutoResponse> listarPorParceiro (Long partnerId)
    {
        // Assume que findByPartnerId retorna List<Produto>
        return produtoRepository.findByPartnerId(partnerId)
                .stream()
                .map(produtoMapper::toResponse) // Usa o mapper
                .collect(Collectors.toList()); // Usando collect(Collectors.toList()) para compatibilidade com Java 8/11
    }

    // OBSERVAÇÃO: Seu repositório precisará ter um método findByPartnerId que retorne List<Produto>
}
