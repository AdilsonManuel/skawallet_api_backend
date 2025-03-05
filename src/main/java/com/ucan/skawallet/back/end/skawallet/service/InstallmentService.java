/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.enums.InstallmentStatus;
import com.ucan.skawallet.back.end.skawallet.model.DigitalWallets;
import com.ucan.skawallet.back.end.skawallet.model.Installment;
import com.ucan.skawallet.back.end.skawallet.model.Transactions;
import com.ucan.skawallet.back.end.skawallet.repository.DigitalWalletRepository;
import com.ucan.skawallet.back.end.skawallet.repository.InstallmentRepository;
import com.ucan.skawallet.back.end.skawallet.repository.TransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author AdilsonManuel
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InstallmentService
{

    private final InstallmentRepository installmentRepository;
    private final TransactionRepository transactionRepository;
    private final DigitalWalletRepository digitalWalletRepository;

    public List<Installment> createInstallments(Long transactionId, int installmentsCount, BigDecimal interestRate)
    {
        Transactions transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));

        List<Installment> installments = new ArrayList<>();
        BigDecimal installmentAmount = transaction.getAmount()
                .add(transaction.getAmount().multiply(interestRate))
                .divide(BigDecimal.valueOf(installmentsCount), 2, RoundingMode.HALF_UP);

        for (int i = 1; i <= installmentsCount; i++)
        {
            Installment installment = new Installment();
            installment.setTransaction(transaction);
            installment.setUser(transaction.getSourceWallet().getUser());
            installment.setAmount(installmentAmount);
            installment.setDueDate(LocalDateTime.now().plusMonths(i));
            installment.setStatus(InstallmentStatus.PENDING);
            installments.add(installment);
        }

        return installmentRepository.saveAll(installments);
    }

    public void payInstallment(Long installmentId)
    {
        Installment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        List<DigitalWallets> wallet = digitalWalletRepository.findByUser(installment.getUser());

        if (wallet.isEmpty())
        {  
            throw new RuntimeException("Nenhuma carteira encontrada para este usuário.");
        }

        DigitalWallets userWallet = wallet.get(0); // Pegando o primeiro item

        if (userWallet.getBalance().compareTo(installment.getAmount()) < 0)
        {
            throw new RuntimeException("Saldo insuficiente para pagar a parcela.");
        }

        userWallet.setBalance(userWallet.getBalance().subtract(installment.getAmount()));
        installment.setStatus(InstallmentStatus.PAID);

        digitalWalletRepository.save(userWallet);
        installmentRepository.save(installment);
    }

}
