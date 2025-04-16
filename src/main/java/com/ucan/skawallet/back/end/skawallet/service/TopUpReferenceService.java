/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.enums.TopUpStatus;
import com.ucan.skawallet.back.end.skawallet.model.DigitalWallets;
import com.ucan.skawallet.back.end.skawallet.model.TopUpReference;
import com.ucan.skawallet.back.end.skawallet.repository.DigitalWalletRepository;
import com.ucan.skawallet.back.end.skawallet.repository.TopUpReferenceRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TopUpReferenceService
{

    private final TopUpReferenceRepository topUpReferenceRepository;
    private final DigitalWalletRepository digitalWalletRepository;

    public TopUpReference generateReference (String walletCode, BigDecimal amount)
    {
        System.err.println("Wallet Code -> " + walletCode);
        DigitalWallets wallet = digitalWalletRepository.getWalletByCode(walletCode)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada"));

        String code = "REF-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) + "-WALLET-" + walletCode;

        TopUpReference reference = TopUpReference.builder()
                .referenceCode(code)
                .wallet(wallet)
                .amount(amount)
                .expiresAt(LocalDateTime.now().plusHours(2))
                .build();

        return topUpReferenceRepository.save(reference);
    }

    public TopUpReference confirmTopUp (String referenceCode)
    {
        TopUpReference reference = topUpReferenceRepository.findByReferenceCode(referenceCode)
                .orElseThrow(() -> new RuntimeException("Referência inválida"));

        if (reference.getStatus() != TopUpStatus.PENDING)
        {
            throw new RuntimeException("Referência já utilizada ou expirada");
        }

        if (reference.getExpiresAt().isBefore(LocalDateTime.now()))
        {
            reference.setStatus(TopUpStatus.EXPIRED);
            topUpReferenceRepository.save(reference);
            throw new RuntimeException("Referência expirada");
        }

        // Atualiza status
        reference.setStatus(TopUpStatus.COMPLETED);
        topUpReferenceRepository.save(reference);

        // Adiciona saldo à carteira
        DigitalWallets wallet = reference.getWallet();
        wallet.setBalance(wallet.getBalance().add(reference.getAmount()));
        digitalWalletRepository.save(wallet);

        return reference;
    }
}
