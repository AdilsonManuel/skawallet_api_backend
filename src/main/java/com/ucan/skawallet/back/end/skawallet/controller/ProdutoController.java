/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.controller;

import com.ucan.skawallet.back.end.skawallet.dto.ProdutoResponse;
import com.ucan.skawallet.back.end.skawallet.model.Produto;
import com.ucan.skawallet.back.end.skawallet.service.ProdutoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author azm
 */
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController
{

    @Autowired
    private ProdutoService produtoService;

    @PostMapping("/")
    public ResponseEntity<Produto> criar (@RequestBody Produto produto)
    {
        // Geralmente é melhor usar DTOs aqui também (ProdutoRequestDTO), mas mantemos a Entidade para compatibilidade com o service.
        return ResponseEntity.ok(produtoService.salvarProduto(produto));
    }

    // CORRIGIDO: Retorna List<ProdutoResponse> para resolver o erro de incompatibilidade de tipos
    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listarTodos ()
    {
        // O service agora retorna List<ProdutoResponse>, então o controller deve esperar isso.
        return ResponseEntity.ok(produtoService.listarProdutos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId (@PathVariable Long id)
    {
        // Este método busca uma única entidade e pode retornar a Entidade, mas atenção à serialização recursiva aqui.
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar (@PathVariable Long id)
    {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/partner/{partnerId}")
    public ResponseEntity<List<ProdutoResponse>> listarPorPartner (@PathVariable Long partnerId)
    {
        // Já estava correto, retornando List<ProdutoResponse>
        return ResponseEntity.ok(produtoService.listarPorParceiro(partnerId));
    }
}
