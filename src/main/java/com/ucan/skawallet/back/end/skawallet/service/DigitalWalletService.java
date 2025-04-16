/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.dto.DigitalWalletDTO;
import com.ucan.skawallet.back.end.skawallet.model.DigitalWallets;
import com.ucan.skawallet.back.end.skawallet.model.Users;
import com.ucan.skawallet.back.end.skawallet.repository.DigitalWalletRepository;
import com.ucan.skawallet.back.end.skawallet.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DigitalWalletService
{

    private final DigitalWalletRepository digitalWalletRepository;
    private final UserRepository userRepository;

    // Criar uma nova carteira digital
    public DigitalWallets createWallet (DigitalWalletDTO walletDTO)
    {
//        System.err.println("wallet DTO: " + walletDTO);
        Users user = userRepository.findById(walletDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        DigitalWallets wallet = new DigitalWallets();
        wallet.setWalletName(walletDTO.getWalletName());
        wallet.setWalletType(walletDTO.getWalletType());
        wallet.setBalance(walletDTO.getBalance() != null ? walletDTO.getBalance() : BigDecimal.ZERO);
        wallet.setCurrency(walletDTO.getCurrency() != null ? walletDTO.getCurrency() : "AKZ");
        wallet.setIsDefault(walletDTO.getIsDefault() != null ? walletDTO.getIsDefault() : false);
        wallet.setUser(user);

        // Gerar código único
        wallet.setWalletCode(generateUniqueWalletCode(user.getPkUsers()));

        return digitalWalletRepository.save(wallet);
    }

    private String generateUniqueWalletCode (Long userId)
    {
        return "SKA-" + userId + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Obter todas as carteiras de um usuário
    public List<DigitalWallets> getWalletsByUser (Long userId)
    {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        return digitalWalletRepository.findByUser(user);
    }

    // Obter carteira por ID
    public DigitalWallets findById (Long walletId)
    {
        return digitalWalletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada."));
    }

    // Atualizar parcialmente uma carteira
    public DigitalWallets updateWallet (Long walletId, DigitalWalletDTO walletDTO)
    {
        //        System.err.println("wallet DTO: " + walletDTO);
        Users user = userRepository.findById(walletDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        DigitalWallets wallet = findById(walletId);

        if (walletDTO.getWalletName() != null)
        {
            wallet.setWalletName(walletDTO.getWalletName());
        }
        if (walletDTO.getWalletType() != null)
        {
            wallet.setWalletType(walletDTO.getWalletType());
        }
        if (walletDTO.getCurrency() != null)
        {
            wallet.setCurrency(walletDTO.getCurrency());
        }
        if (walletDTO.getIsDefault() != null)
        {
            wallet.setIsDefault(walletDTO.getIsDefault());
        }

        if (wallet.getWalletCode() == null || wallet.getWalletCode().isEmpty())
        {
            String generatedCode = generateUniqueWalletCode(user.getPkUsers());
            walletDTO.setWalletCode(generatedCode);
            wallet.setWalletCode(generatedCode);
        }

        wallet.setUpdatedAt(LocalDateTime.now());
        return digitalWalletRepository.save(wallet);
    }

    // Deletar uma carteira
    public void deleteWallet (Long walletId)
    {
        DigitalWallets wallet = findById(walletId);
        digitalWalletRepository.delete(wallet);
    }

    /**
     * Consulta o saldo de uma carteira específica de um usuário.
     *
     * @param walletId ID da carteira.
     * @param userId ID do usuário.
     * @return Saldo da carteira.
     */
    public BigDecimal getWalletBalance (Long walletId, Long userId)
    {
        // Verificar se a carteira pertence ao usuário
        DigitalWallets wallet = digitalWalletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada ou não pertence ao usuário."));

        // Retornar o saldo
        return wallet.getBalance();
    }

    public DigitalWallets getWalletByCode (String walletCode)
    {
        return digitalWalletRepository.getWalletByCode(walletCode)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada com o código: " + walletCode));
    }

    public List<DigitalWallets> getAllWallets ()
    {
        return digitalWalletRepository.findAll();
    }

}
