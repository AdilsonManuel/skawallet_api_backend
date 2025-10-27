/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.dto.InstallmentHistoryDTO;
import com.ucan.skawallet.back.end.skawallet.dto.InstallmentRequestDTO;
import com.ucan.skawallet.back.end.skawallet.enums.InstallmentStatus;
import com.ucan.skawallet.back.end.skawallet.enums.PaymentMethod;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionStatus;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionType;
import com.ucan.skawallet.back.end.skawallet.model.DigitalWallets;
import com.ucan.skawallet.back.end.skawallet.model.Installment;
import com.ucan.skawallet.back.end.skawallet.model.Partner;
import com.ucan.skawallet.back.end.skawallet.model.Produto;
import com.ucan.skawallet.back.end.skawallet.model.Transactions;
import com.ucan.skawallet.back.end.skawallet.model.Users;
import com.ucan.skawallet.back.end.skawallet.repository.DigitalWalletRepository;
import com.ucan.skawallet.back.end.skawallet.repository.InstallmentRepository;
import com.ucan.skawallet.back.end.skawallet.repository.PartnerRepository;
import com.ucan.skawallet.back.end.skawallet.repository.ProdutoRepository;
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
import java.util.Optional;
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
    private final ProdutoRepository produtoRepository;

    @Transactional
    public Installment createInstallment (InstallmentRequestDTO request)
    {
        Users user = usersRepository.findByIdDocument(request.getIdDocument())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        verificarBloqueioUsuario(user);

        if (Boolean.TRUE.equals(user.getBlockedByInadimplencia()))
        {
            throw new IllegalStateException("Operação não permitida. BI bloqueado.");
        }

        Partner partner = partnersRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new RuntimeException("Parceiro não encontrado."));

        // ✅ Busca o produto e valida se pertence ao parceiro
        Produto produto = produtoRepository.findById(request.getProductId())
                // 🛑 MUDAR RuntimeException para EntityNotFoundException
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

        if (!produto.getPartner().getPkPartners().equals(partner.getPkPartners()))
        {
            throw new IllegalArgumentException("Produto informado não pertence ao parceiro selecionado.");
        }

        validateUserEligibility(user);

        int approvedInstallments = Math.min(request.getInstallments(), 6);
        BigDecimal totalAmount = produto.getPreco(); // Usa o preço do produto
        BigDecimal monthlyPayment = totalAmount
                .divide(BigDecimal.valueOf(approvedInstallments), RoundingMode.HALF_UP);

        DigitalWallets wallet = digitalWalletRepository.getWalletByCode(request.getWalletCode())
                .orElseThrow(() -> new EntityNotFoundException("Carteira não encontrada."));

        if (wallet.getBalance().compareTo(monthlyPayment) < 0)
        {
            throw new IllegalStateException("Saldo insuficiente para iniciar parcelamento.");
        }

        wallet.setBalance(wallet.getBalance().subtract(monthlyPayment));
        digitalWalletRepository.save(wallet);

        Installment installment = new Installment();
        installment.setUser(user);
        installment.setPartner(partner);
        installment.setProduto(produto); // ✅ vincula o produto ao parcelamento
        installment.setTotalAmount(totalAmount);
        installment.setMonthlyPayment(monthlyPayment);
        installment.setInstallments(approvedInstallments);
        installment.setRemainingInstallments(approvedInstallments - 1);
        installment.setMonthlyPayment(monthlyPayment);
        installment.setNextDueDate(LocalDate.now().plusMonths(1));
        installment.setStatus(InstallmentStatus.PENDING);

        installmentRepository.save(installment);

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

        return installment;
    }

    public List<Installment> getUserInstallments (Long userId)
    {
        return installmentRepository.findByUserPkUsers(userId);
    }

    @Transactional
    public String payInstallment (Long installmentId, String walletCode)
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

        BigDecimal monthlyPayment = installment.getMonthlyPayment();

        // Verifica saldo suficiente
        if (wallet.getBalance().compareTo(monthlyPayment) < 0)
        {
            throw new IllegalStateException("Saldo insuficiente para pagamento da parcela.");
        }

        // Debita o valor da carteira
        wallet.setBalance(wallet.getBalance().subtract(monthlyPayment));
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
            installment.setStatus(InstallmentStatus.PENDING);
        }

        installmentRepository.save(installment);

        // Criar registro da transação
        Transactions tx = new Transactions();
        tx.setAmount(monthlyPayment);
        tx.setTransactionType(TransactionType.INSTALLMENT_PAYMENT);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setCreatedAt(LocalDateTime.now());
        tx.setCompletedAt(LocalDateTime.now());
        tx.setSourceWallet(wallet);
        tx.setPaymentMethod(PaymentMethod.DIGITAL_WALLET);
        tx.setDescription("Pagamento da parcela do parcelamento ID " + installmentId);
        transactionRepository.save(tx);

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

    ///Teste

    private void validateUserEligibility (Users user)
    {
        int userScore = calculateUserScore(user); // pode ser usado no futuro para scoring mais avançado
        long transactionCount = transactionRepository.countTransactionsByUserId(user.getPkUsers());

        if (transactionCount < 5)
        {
            throw new RuntimeException("Usuário não elegível para parcelamento. Necessário ter no mínimo 5 transações, atualmente tem: " + transactionCount);
        }

        List<Installment> activeInstallments = installmentRepository.findByUserAndStatus(user, InstallmentStatus.PENDING);
        int activeCount = activeInstallments.size();

        LocalDate carteiraCriadaEm = user.getCreatedAt().toLocalDate();
        long mesesAtivo = ChronoUnit.MONTHS.between(carteiraCriadaEm, LocalDate.now());

        if (mesesAtivo < 6)
        {
            // Cliente novo
            if (activeCount > 0)
            {
                throw new RuntimeException("Usuário novo só pode ter 1 parcelamento ativo por vez.");
            }
        }
        else
        {
            // Cliente com mais de 6 meses (antigo)
            if (activeCount >= 3)
            {
                throw new RuntimeException("Usuário já possui o número máximo de parcelamentos ativos (3).");
            }
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

        // Buscar a carteira padrão do usuário
        Optional<DigitalWallets> optionalWallet = digitalWalletRepository.findByUserPkUsersAndIsDefaultTrue(user.getPkUsers());

        if (optionalWallet.isEmpty())
        {
            // Logar ou lançar uma exceção conforme sua regra de negócio
            System.out.println("Usuário não possui carteira padrão.");
            return;
        }

        DigitalWallets wallet = optionalWallet.get();

        for (Installment parcela : vencidas)
        {
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

    public List<InstallmentHistoryDTO> getUserInstallmentHistory (Long userId)
    {
        return installmentRepository.findInstallmentsByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private InstallmentHistoryDTO convertToDto (Installment installment)
    {
        return InstallmentHistoryDTO.builder()
                .installmentId(installment.getInstallmentId())
                .partnerName(installment.getPartner().getName())
                .productName(Optional.ofNullable(installment.getProduto())
                        .map(Produto::getNome)
                        .orElse("Produto não definido"))
                .productPrice(Optional.ofNullable(installment.getProduto())
                        .map(Produto::getPreco)
                        .orElse(null))
                .totalAmount(installment.getTotalAmount())
                .installments(installment.getInstallments())
                .remainingInstallments(installment.getRemainingInstallments())
                .monthlyPayment(installment.getMonthlyPayment())
                .nextDueDate(installment.getNextDueDate())
                .status(installment.getStatus().name())
                .build();
    }

}
