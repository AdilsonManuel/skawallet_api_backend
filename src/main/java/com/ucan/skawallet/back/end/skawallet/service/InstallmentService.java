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
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

        // Verifica se o usuário é elegível para parcelamento
        validateUserEligibility(user);

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

    @Transactional
    public String payInstallment (Long installmentId, String walletCode, BigDecimal amount)
    {
        // Busca o parcelamento pelo ID
        Installment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new EntityNotFoundException("Parcelamento não encontrado."));

        // Verifica se o parcelamento já foi quitado
        if (installment.getRemainingInstallments() == 0)
        {
            return "Não há parcelas pendentes para pagamento.";
        }

        // Busca a carteira pelo código
        DigitalWallets wallet = digitalWalletRepository.getWalletByCode(walletCode)
                .orElseThrow(() -> new EntityNotFoundException("Carteira não encontrada."));

        // Verifica saldo suficiente
        if (wallet.getBalance().compareTo(amount) < 0)
        {
            throw new IllegalStateException("Saldo insuficiente para pagamento da parcela.");
        }

        // Debita o valor da carteira
        wallet.setBalance(wallet.getBalance().subtract(amount));
        digitalWalletRepository.save(wallet);

        // Atualiza o parcelamento
        installment.setRemainingInstallments(installment.getRemainingInstallments() - 1);
        installment.setNextDueDate(LocalDate.now().plusMonths(1)); // Atualiza a próxima data de vencimento

        // Verifica se o parcelamento pode ser marcado como COMPLETED
        if (installment.getRemainingInstallments() == 0)
        {
            installment.setStatus(InstallmentStatus.COMPLETED);
        }
        else
        {
            // Se ainda há parcelas, não altera o status
            installment.setStatus(InstallmentStatus.PENDING); // Exemplo de status alternativo, se necessário
        }

        // Salva as alterações no parcelamento
        installmentRepository.save(installment);

        return "Parcelamento pago com sucesso! Restam " + installment.getRemainingInstallments() + " parcelas.";
    }

    public int calculateUserScore (Users user)
    {
        int score = 0;

        // ✅ Converter LocalDateTime para LocalDate
        LocalDate createdAt = user.getCreatedAt().toLocalDate();
        long accountAgeInDays = ChronoUnit.DAYS.between(createdAt, LocalDate.now());

        score += (accountAgeInDays / 30) * 5; // 5 pontos a cada mês de conta ativa

        // ✅ Histórico de Parcelamentos (se pagou tudo sem atraso, ganha pontos extras)
        boolean hasGoodHistory = installmentRepository.countLatePaymentsByUser(user) == 0;
        if (hasGoodHistory)
        {
            score += 30; // Bônus por bom comportamento financeiro
        }

        return score;
    }

    /**
     * Método que verifica se um usuário pode solicitar um parcelamento.
     */
    private void validateUserEligibility (Users user)
    {
        int userScore = calculateUserScore(user);
        long transactionCount = transactionRepository.countTransactionsByUserId(user.getPkUsers());

        if (transactionCount < 5)
        {
            throw new RuntimeException("Usuário não elegível para parcelamento. Necessário ter no mínimo 5 transações, actualmente tem: " + transactionCount);
        }
    }

    public List<Installment> getAllInstallments ()
    {
        return installmentRepository.findAll();
    }
}
