/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.dto.DepositRequestDTO;
import com.ucan.skawallet.back.end.skawallet.dto.TransactionDTO;
import com.ucan.skawallet.back.end.skawallet.dto.TransactionResponseDTO;
import com.ucan.skawallet.back.end.skawallet.dto.UserHistoryDTO;
import com.ucan.skawallet.back.end.skawallet.enums.EventType;
import com.ucan.skawallet.back.end.skawallet.enums.PaymentMethod;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionStatus;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionType;
import com.ucan.skawallet.back.end.skawallet.model.DigitalWallets;
import com.ucan.skawallet.back.end.skawallet.model.TopUpReference;
import com.ucan.skawallet.back.end.skawallet.model.Transactions;
import com.ucan.skawallet.back.end.skawallet.repository.DigitalWalletRepository;
import com.ucan.skawallet.back.end.skawallet.repository.TopUpReferenceRepository;
import com.ucan.skawallet.back.end.skawallet.repository.TransactionRepository;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService
{

    private final TransactionRepository transactionRepository;
    private final DigitalWalletRepository digitalWalletRepository;
    private final TransactionHistoryService transactionHistoryService;
    private final TopUpReferenceRepository topUpReferenceRepository;

    public List<Transactions> ListTransactions ()
    {
        return transactionRepository.findAll();
    }

    // Criar uma nova transação
    @Transactional
    public Transactions createTransaction (TransactionDTO transactionDTO)
    {
        // Validar e buscar carteiras de origem e destino
        DigitalWallets sourceWallet = transactionDTO.getSourceWalletId() != null
                ? digitalWalletRepository.findById(transactionDTO.getSourceWalletId())
                        .orElseThrow(() -> new RuntimeException("Carteira de origem não encontrada."))
                : null;

        DigitalWallets destinationWallet = transactionDTO.getDestinationWalletId() != null
                ? digitalWalletRepository.findById(transactionDTO.getDestinationWalletId())
                        .orElseThrow(() -> new RuntimeException("Carteira de destino não encontrada."))
                : null;

        // Validar saldo para retirada ou transferência
        if (transactionDTO.getTransactionType() == TransactionType.WITHDRAWAL
                || transactionDTO.getTransactionType() == TransactionType.TRANSFER
                || transactionDTO.getTransactionType() == TransactionType.PAYMENT)
        {
            if (sourceWallet == null || sourceWallet.getBalance().compareTo(transactionDTO.getAmount()) < 0)
            {
                throw new RuntimeException("Saldo insuficiente na carteira de origem.");
            }
        }

        if (null != transactionDTO.getTransactionType())
        // Atualizar saldos das carteiras
        {
            switch (transactionDTO.getTransactionType())
            {
                case TRANSFER ->
                {
                    sourceWallet.setBalance(sourceWallet.getBalance().subtract(transactionDTO.getAmount()));
                    destinationWallet.setBalance(destinationWallet.getBalance().add(transactionDTO.getAmount()));
                }
                case DEPOSIT ->
                    destinationWallet.setBalance(destinationWallet.getBalance().add(transactionDTO.getAmount()));
                case WITHDRAWAL ->
                    sourceWallet.setBalance(sourceWallet.getBalance().subtract(transactionDTO.getAmount()));

                case PAYMENT ->
                {
                    if (destinationWallet == null)
                    {
                        throw new RuntimeException("Carteira do comerciante não encontrada.");
                    }
                    sourceWallet.setBalance(sourceWallet.getBalance().subtract(transactionDTO.getAmount()));
                    destinationWallet.setBalance(destinationWallet.getBalance().add(transactionDTO.getAmount()));
                }
                default ->
                {
                }
            }
        }

        // Persistir alterações nas carteiras
        if (sourceWallet != null)
        {
            digitalWalletRepository.save(sourceWallet);
        }
        if (destinationWallet != null)
        {
            digitalWalletRepository.save(destinationWallet);
        }

        // Criar e salvar a transação
        Transactions transaction = new Transactions();
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setTransactionType(transactionDTO.getTransactionType());
        transaction.setPaymentMethod(transactionDTO.getPaymentMethod());
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setStatus(transactionDTO.getStatus());
        transaction.setSourceWallet(sourceWallet);
        transaction.setDestinationWallet(destinationWallet);
        transactionRepository.save(transaction);

        transactionHistoryService.saveHistory(transaction, EventType.CREATED);
        return transaction;
    }

    // Obter transações por carteira
    public List<Transactions> getTransactionsByWallet (Long walletId)
    {
        DigitalWallets wallet = digitalWalletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada."));
        return transactionRepository.findBySourceWalletOrDestinationWallet(wallet, wallet);
    }

    // Obter transação por ID
    public Transactions getTransactionById (Long transactionId)
    {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada."));
    }

    public List<TransactionResponseDTO> getTransactionsByUserId (Long userId)
    {
        return transactionRepository.findTransactionsByUserId(userId).stream()
                .map(result -> new TransactionResponseDTO(
                ((Number) result[0]).longValue(), // transactionId
                (BigDecimal) result[1], // amount
                TransactionType.valueOf((String) result[2]), // transactionType
                TransactionStatus.valueOf((String) result[3]), // status
                ((Timestamp) result[4]).toLocalDateTime(), // createdAt
                result[5] != null ? ((Timestamp) result[5]).toLocalDateTime() : null, // completedAt
                (String) result[6], // sourceWalletName
                (String) result[7], // destinationWalletName
                result[8] != null ? PaymentMethod.valueOf((String) result[8]) : null, // paymentMethod
                (String) result[9] // description
        ))
                .collect(Collectors.toList());
    }

    public Transactions updateTransaction (Transactions transaction)
    {
        // Atualizar a transação no banco de dados
        Transactions updatedTransaction = transactionRepository.save(transaction);

        // Registrar o histórico com o evento "UPDATED"
        transactionHistoryService.saveHistory(updatedTransaction, EventType.UPDATED);

        return updatedTransaction;
    }

    public void deleteTransaction (Long transactionId)
    {
        // Encontrar e deletar a transação
        Transactions transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
        transactionRepository.delete(transaction);

        // Registrar o histórico com o evento "DELETED"
        transactionHistoryService.saveHistory(transaction, EventType.DELETED);
    }

    public List<Transactions> getTransactionsByUserAndDateRange (Long userId, LocalDateTime startDate, LocalDateTime endDate)
    {
        return transactionRepository.findTransactionsByUserAndDateRange(userId, startDate, endDate);
    }

    public Transactions updateTransactionStatus (Long transactionId, TransactionStatus newStatus)
    {
        Transactions transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));

        transaction.setStatus(newStatus);
        transactionRepository.save(transaction);

        return transaction;
    }

    public String generateWithdrawalCode (String walletCode, BigDecimal amount)
    {
        DigitalWallets wallet = digitalWalletRepository.getWalletByCode(walletCode)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada"));

        if (wallet.getBalance().compareTo(amount) < 0)
        {
            throw new RuntimeException("Saldo insuficiente");
        }

        String withdrawalCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Transactions transaction = new Transactions();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setSourceWallet(wallet);
        transaction.setPaymentMethod(PaymentMethod.DIGITAL_WALLET);
        transaction.setDescription("Saque via ATM - Código: " + withdrawalCode);
        transactionRepository.save(transaction);

        wallet.setBalance(wallet.getBalance().subtract(amount));
        digitalWalletRepository.save(wallet);

        return withdrawalCode;
    }

    public Transactions transferFunds (String sourceWalletCode, String destinationWalletCode, BigDecimal amount)
    {
        DigitalWallets sourceWallet = digitalWalletRepository.getWalletByCode(sourceWalletCode)
                .orElseThrow(() -> new RuntimeException("Carteira de origem não encontrada"));

        DigitalWallets destinationWallet = digitalWalletRepository.getWalletByCode(destinationWalletCode)
                .orElseThrow(() -> new RuntimeException("Carteira de destino não encontrada"));

        if (sourceWallet.getBalance().compareTo(amount) < 0)
        {
            throw new RuntimeException("Saldo insuficiente para transferência");
        }

        sourceWallet.setBalance(sourceWallet.getBalance().subtract(amount));
        destinationWallet.setBalance(destinationWallet.getBalance().add(amount));

        digitalWalletRepository.save(sourceWallet);
        digitalWalletRepository.save(destinationWallet);

        // Criar e salvar a transação
        Transactions transaction = new Transactions();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setSourceWallet(sourceWallet);
        transaction.setDestinationWallet(destinationWallet);
        transaction.setDescription("Transferência entre carteiras digitais");
        transactionRepository.save(transaction);

        transactionHistoryService.saveHistory(transaction, EventType.CREATED);
        return transaction;
    }

    @Transactional
    public Transactions topUpWallet (DepositRequestDTO request)
    {

        log.info("Iniciando carregamento da carteira: {}", request);

        // Validar carteira
        DigitalWallets wallet = digitalWalletRepository.getWalletByCode(request.getWalletCode())
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada para o código fornecido"));

        // Simular validação de cartão (Em um cenário real, aqui chamaria a API do banco)
        if (!validateCard(request.getCardNumber(), request.getExpiryDate(), request.getCvv()))
        {
            throw new RuntimeException("Falha na validação do cartão. Verifique os dados e tente novamente.");
        }

        // Atualizar saldo da carteira
        BigDecimal newBalance = wallet.getBalance().add(request.getAmount());
        wallet.setBalance(newBalance);
        digitalWalletRepository.save(wallet);

        // Registrar transação
        Transactions transaction = new Transactions();
        transaction.setAmount(request.getAmount());
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setDestinationWallet(wallet);
        transaction.setPaymentMethod(PaymentMethod.CARD);
        transaction.setDescription("Carregamento de carteira via cartão " + maskCardNumber(request.getCardNumber()));

        transactionRepository.save(transaction);

        log.info("Carregamento concluído com sucesso. Novo saldo: {}", newBalance);
        return transaction;
    }

    // Simulação de validação de cartão
    private boolean validateCard (String cardNumber, String expiryDate, String cvv)
    {
        log.info("Validando cartão de crédito...");
        return cardNumber.length() == 16 && expiryDate.matches("(0[1-9]|1[0-2])\\/(\\d{2})") && cvv.length() == 3;
    }

    // Método para mascarar o número do cartão ao armazenar em logs
    private String maskCardNumber (String cardNumber)
    {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    public List<UserHistoryDTO> getUnifiedUserHistory (Long userId)
    {
        List<Transactions> transactions = transactionRepository.findByUserId(userId);
        List<TopUpReference> topUps = topUpReferenceRepository.findConfirmedByUserId(userId);

        List<UserHistoryDTO> history = new ArrayList<>();

        // Todas as transações disponíveis no enum TransactionType
        for (Transactions t : transactions)
        {
            if (t.getTransactionType() != null)
            {
                history.add(UserHistoryDTO.builder()
                        .type(t.getTransactionType().name())
                        .amount(t.getAmount())
                        .description(t.getDescription())
                        .date(t.getCreatedAt())
                        .build());
            }
        }

        // Recargas via referência (TOP_UP)
        for (TopUpReference topUp : topUps)
        {
            history.add(UserHistoryDTO.builder()
                    .type(TransactionType.TOP_UP.name())
                    .amount(topUp.getAmount())
                    .description("Recarga via referência")
                    .date(topUp.getCreatedAt())
                    .build());
        }

        // Ordenar por data (mais recente primeiro)
        history.sort(Comparator.comparing(UserHistoryDTO::getDate).reversed());

        return history;
    }

}
