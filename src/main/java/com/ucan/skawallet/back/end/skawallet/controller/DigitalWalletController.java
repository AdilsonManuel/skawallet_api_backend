/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/SpringFramework/Controller.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.controller;

import com.ucan.skawallet.back.end.skawallet.dto.DigitalWalletDTO;
import com.ucan.skawallet.back.end.skawallet.model.DigitalWallets;
import com.ucan.skawallet.back.end.skawallet.service.DigitalWalletService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class DigitalWalletController
{

    private final DigitalWalletService digitalWalletService;

    // Criar uma nova carteira digital
    @PostMapping("/")
    public ResponseEntity<DigitalWallets> createWallet (@RequestBody DigitalWalletDTO walletDTO)
    {
        DigitalWallets wallet = digitalWalletService.createWallet(walletDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
    }

    // Obter todas as carteiras de um usuário
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DigitalWallets>> getWalletsByUser (@PathVariable Long userId)
    {
        List<DigitalWallets> wallets = digitalWalletService.getWalletsByUser(userId);
        return ResponseEntity.ok(wallets);
    }

    // Obter detalhes de uma carteira específica
    @GetMapping("/{walletId}")
    public ResponseEntity<DigitalWallets> getWalletById (@PathVariable Long walletId)
    {
        DigitalWallets wallet = digitalWalletService.findById(walletId);
        return ResponseEntity.ok(wallet);
    }

    // Atualizar parcialmente uma carteira
    @PatchMapping("/{walletId}")
    public ResponseEntity<DigitalWallets> updateWallet (
            @PathVariable Long walletId,
            @RequestBody DigitalWalletDTO walletDTO)
    {
        DigitalWallets updatedWallet = digitalWalletService.updateWallet(walletId, walletDTO);
        return ResponseEntity.ok(updatedWallet);
    }

    // Deletar uma carteira
    @DeleteMapping("/{walletId}")
    public ResponseEntity<String> deleteWallet (@PathVariable Long walletId)
    {
        digitalWalletService.deleteWallet(walletId);
        return ResponseEntity.ok("Carteira deletada com sucesso.");
    }

    @GetMapping("/balance/{walletId}")
    public ResponseEntity<Map<String, Object>> getWalletBalance (@PathVariable String walletId)
    {
        System.err.println("com.ucan.skawallet.back.end.skawallet.controller.DigitalWalletController.getWalletBalance()" + walletId);
        DigitalWallets digitalWallets = digitalWalletService.getWalletByCode(walletId);

        Map<String, Object> response = new HashMap<>();
        response.put("walletId", digitalWallets.getPk_digital_wallets());
        response.put("balance", digitalWallets.getBalance());
        response.put("currency", digitalWallets.getCurrency());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{walletCode}")
    public ResponseEntity<DigitalWallets> getWalletByCode (@PathVariable String walletCode)
    {
        return ResponseEntity.ok(digitalWalletService.getWalletByCode(walletCode));
    }

    @GetMapping("/")
    public ResponseEntity<List<DigitalWallets>> getAllWallets ()
    {
        List<DigitalWallets> wallets = digitalWalletService.getAllWallets();
        return ResponseEntity.ok(wallets);
    }

}
