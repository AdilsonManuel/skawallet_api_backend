/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.dto.InstallmentRequestDTO;
import com.ucan.skawallet.back.end.skawallet.enums.InstallmentStatus;
import com.ucan.skawallet.back.end.skawallet.enums.PaymentMethod;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionStatus;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionType;
import com.ucan.skawallet.back.end.skawallet.model.DigitalWallets;
import com.ucan.skawallet.back.end.skawallet.model.Installment;
import com.ucan.skawallet.back.end.skawallet.model.Partner;
import com.ucan.skawallet.back.end.skawallet.model.Transactions;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
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

    @Transactional
    public Installment createInstallment (InstallmentRequestDTO request)
    {
        Users user = usersRepository.findByIdDocument(request.getIdDocument())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // ✅ Verifica se o usuário está bloqueado por inadimplência antes de qualquer ação
        verificarBloqueioUsuario(user);

        // Verifica se o usuário não está bloqueado por inadimplência
        if (Boolean.TRUE.equals(user.getBlockedByInadimplencia()))
        {
            throw new IllegalStateException("Operação não permitida. BI bloqueado.");
        }

        Partner partner = partnersRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new RuntimeException("Parceiro não encontrado."));

        validateUserEligibility(user);

        int approvedInstallments = Math.min(request.getInstallments(), 6);
        BigDecimal monthlyPayment = request.getAmount()
                .divide(BigDecimal.valueOf(approvedInstallments), RoundingMode.HALF_UP);

        // Verifica e debita a primeira parcela
        DigitalWallets wallet = digitalWalletRepository.getWalletByCode(request.getWalletCode())
                .orElseThrow(() -> new EntityNotFoundException("Carteira não encontrada."));

        if (wallet.getBalance().compareTo(monthlyPayment) < 0)
        {
            throw new IllegalStateException("Saldo insuficiente para iniciar parcelamento.");
        }

        // Débito inicial
        wallet.setBalance(wallet.getBalance().subtract(monthlyPayment));
        digitalWalletRepository.save(wallet);

        // Criação da parcela
        Installment installment = new Installment();
        installment.setUser(user);
        installment.setPartner(partner);
        installment.setTotalAmount(request.getAmount());
        installment.setInstallments(approvedInstallments);
        installment.setRemainingInstallments(approvedInstallments - 1); // primeira já paga
        installment.setMonthlyPayment(monthlyPayment);
        installment.setNextDueDate(LocalDate.now().plusMonths(1));
        installment.setStatus(InstallmentStatus.PENDING);

        installmentRepository.save(installment);

        // Transação inicial
        Transactions tx = new Transactions();
        tx.setAmount(monthlyPayment);
        tx.setTransactionType(TransactionType.INSTALLMENT_PAYMENT);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setCreatedAt(LocalDateTime.now());
        tx.setCompletedAt(LocalDateTime.now());
        tx.setSourceWallet(wallet);
        tx.setPaymentMethod(PaymentMethod.DIGITAL_WALLET);
        tx.setDescription("Pagamento da 1ª parcela do parcelamento");
        transactionRepository.save(tx);

        // ✅ Tenta debitar parcelas vencidas (caso o usuário tenha outras pendentes)
        debitarParcelasVencidasDoUsuario(user);

        return installment;
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

    public void verificarBloqueioUsuario (Users user)
    {
        LocalDate hoje = LocalDate.now();
        boolean possuiAtraso = installmentRepository
                .findByUserPkUsers(user.getPkUsers()).stream()
                .anyMatch(i -> i.getStatus() == InstallmentStatus.PENDING && i.getNextDueDate().isBefore(hoje));

        if (possuiAtraso && !Boolean.TRUE.equals(user.getBlockedByInadimplencia()))
        {
            user.setBlockedByInadimplencia(true);
            usersRepository.save(user);
        }
    }

    public void debitarParcelasVencidasDoUsuario (Users user)
    {
        LocalDate hoje = LocalDate.now();

        List<Installment> vencidas = installmentRepository
                .findByUserPkUsers(user.getPkUsers()).stream()
                .filter(i -> i.getStatus() == InstallmentStatus.PENDING && !i.getNextDueDate().isAfter(hoje))
                .collect(Collectors.toList());

        for (Installment parcela : vencidas)
        {
            DigitalWallets wallet = (DigitalWallets) user.getWallets();
            BigDecimal valor = parcela.getMonthlyPayment();

            if (wallet.getBalance().compareTo(valor) >= 0)
            {
                wallet.setBalance(wallet.getBalance().subtract(valor));
                digitalWalletRepository.save(wallet);

                parcela.setRemainingInstallments(parcela.getRemainingInstallments() - 1);
                parcela.setNextDueDate(hoje.plusMonths(1));

                if (parcela.getRemainingInstallments() == 0)
                {
                    parcela.setStatus(InstallmentStatus.COMPLETED);
                }

                installmentRepository.save(parcela);

                Transactions tx = new Transactions();
                tx.setAmount(valor);
                tx.setTransactionType(TransactionType.INSTALLMENT_PAYMENT);
                tx.setStatus(TransactionStatus.COMPLETED);
                tx.setCreatedAt(LocalDateTime.now());
                tx.setCompletedAt(LocalDateTime.now());
                tx.setSourceWallet(wallet);
                tx.setPaymentMethod(PaymentMethod.DIGITAL_WALLET);
                tx.setDescription("Débito automático de parcela vencida");
                transactionRepository.save(tx);
            }
        }

    }
}
