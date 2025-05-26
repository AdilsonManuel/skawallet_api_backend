/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.controller;

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
// ProdutoController.java
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController
{

    @Autowired
    private ProdutoService produtoService;

    @PostMapping("/")
    public ResponseEntity<Produto> criar (@RequestBody Produto produto)
    {
        return ResponseEntity.ok(produtoService.salvarProduto(produto));
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos ()
    {
        return ResponseEntity.ok(produtoService.listarProdutos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId (@PathVariable Long id)
    {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar (@PathVariable Long id)
    {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/partner/{partnerId}")
    public ResponseEntity<List<Produto>> listarPorPartner (@PathVariable Long partnerId)
    {
        return ResponseEntity.ok(produtoService.listarPorParceiro(partnerId));
    }
}
