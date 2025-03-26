/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.dto.InstallmentRequestDTO;
import com.ucan.skawallet.back.end.skawallet.enums.InstallmentStatus;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionStatus;
import com.ucan.skawallet.back.end.skawallet.model.DigitalWallets;
import com.ucan.skawallet.back.end.skawallet.model.Installment;
import com.ucan.skawallet.back.end.skawallet.model.Partner;
import com.ucan.skawallet.back.end.skawallet.model.Users;
import com.ucan.skawallet.back.end.skawallet.repository.DigitalWalletRepository;
import com.ucan.skawallet.back.end.skawallet.repository.InstallmentRepository;
import com.ucan.skawallet.back.end.skawallet.repository.PartnerRepository;
import com.ucan.skawallet.back.end.skawallet.repository.TransactionRepository;
import com.ucan.skawallet.back.end.skawallet.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author azm
 */
@Service
@RequiredArgsConstructor
public class InstallmentService
{

    private final InstallmentRepository installmentRepository;
    private final UserRepository usersRepository;
    private final PartnerRepository partnersRepository;
    private final DigitalWalletRepository digitalWalletRepository;
    private final TransactionRepository transactionRepository;

    public Installment createInstallment (InstallmentRequestDTO request)
    {
        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Partner partner = partnersRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new RuntimeException("Parceiro não encontrado."));

        // Simulação de score e elegibilidade
        if (!isEligibleForInstallments(user))
        {
            throw new RuntimeException("Usuário não elegível para parcelamento");
        }

        int approvedInstallments = request.getInstallments();
        if (approvedInstallments > 6)
        {
            approvedInstallments = 6;
        }

        BigDecimal monthlyPayment = request.getAmount().divide(BigDecimal.valueOf(approvedInstallments), RoundingMode.HALF_UP);
        LocalDate firstDueDate = LocalDate.now().plusMonths(1);

        Installment installment = new Installment();
        installment.setUser(user);
        installment.setPartner(partner);
        installment.setTotalAmount(request.getAmount());
        installment.setInstallments(approvedInstallments);
        installment.setRemainingInstallments(approvedInstallments);
        installment.setMonthlyPayment(monthlyPayment);
        installment.setNextDueDate(firstDueDate);
        installment.setStatus(InstallmentStatus.APPROVED);

        return installmentRepository.save(installment);
    }

    public List<Installment> getUserInstallments (Long userId)
    {
        return installmentRepository.findByUserPkUsers(userId);
    }

    public Installment payInstallment (Long installmentId, String walletCode, BigDecimal amount)
    {
        Installment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new RuntimeException("Parcelamento não encontrado."));

        DigitalWallets wallet = digitalWalletRepository.getWalletByCode(walletCode)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada."));

        if (wallet.getBalance().compareTo(amount) < 0)
        {
            throw new RuntimeException("Saldo insuficiente.");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        digitalWalletRepository.save(wallet);

        installment.setRemainingInstallments(installment.getRemainingInstallments() - 1);
        installment.setNextDueDate(LocalDate.now().plusMonths(1));

        if (installment.getRemainingInstallments() == 0)
        {
            installment.setStatus(InstallmentStatus.COMPLETED);
        }

        return installmentRepository.save(installment);
    }

    private boolean isEligibleForInstallments (Users user)
    {
        int completedTransactions = transactionRepository.countBySourceWallet_UserOrDestinationWallet_UserAndStatus(
                user, user, TransactionStatus.COMPLETED);
        return completedTransactions >= 5;
    }

    public List<Installment> getAllInstallments ()
    {
        return installmentRepository.findAll();
    }
}
