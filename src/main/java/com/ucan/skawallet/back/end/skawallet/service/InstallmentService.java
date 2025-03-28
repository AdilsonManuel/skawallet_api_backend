/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.dto.InstallmentRequestDTO;
import com.ucan.skawallet.back.end.skawallet.enums.InstallmentStatus;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        // Avaliação do score de crédito do usuário
        int userScore = calculateUserScore(user);
        if (userScore < 70)
        {
            throw new RuntimeException("Usuário não elegível para parcelamento. Score de crédito insuficiente: " + userScore);
        }

        int approvedInstallments = Math.min(request.getInstallments(), 6);

        BigDecimal monthlyPayment = request.getAmount()
                .divide(BigDecimal.valueOf(approvedInstallments), RoundingMode.HALF_UP);
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

    public int calculateUserScore (Users user)
    {
        int score = 0;

        // ✅ Tempo de conta ativa (mais tempo = mais pontos)
        long accountAgeInDays = ChronoUnit.DAYS.between(user.getCreatedAt(), LocalDate.now());
        score += (accountAgeInDays / 30) * 5; // 5 pontos a cada mês de conta ativa

//        // ✅ Pagamentos concluídos
//        int successfulPayments = transactionRepository.countByUserAndStatus(user,TransactionStatus.COMPLETED);
//        score += successfulPayments * 2; // 2 pontos por pagamento bem-sucedido
//        // ✅ Recomendações (cada convite gera pontos)
//        int invitedUsers = referralRepository.countByInviter(user);
//        score += invitedUsers * 10; // 10 pontos por cada amigo indicado
        // ✅ Histórico de Parcelamentos (se pagou tudo sem atraso, ganha pontos extras)
        boolean hasGoodHistory = installmentRepository.countLatePaymentsByUser(user) == 0;
        if (hasGoodHistory)
        {
            score += 30; // Bônus por bom comportamento financeiro
        }

        return score;
    }

    public List<Installment> getAllInstallments ()
    {
        return installmentRepository.findAll();
    }
}
