/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.dto.TransactionDTO;
import com.ucan.skawallet.back.end.skawallet.dto.TransactionResponseDTO;
import com.ucan.skawallet.back.end.skawallet.enums.EventType;
import com.ucan.skawallet.back.end.skawallet.enums.PaymentMethod;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionStatus;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionType;
import com.ucan.skawallet.back.end.skawallet.model.DigitalWallets;
import com.ucan.skawallet.back.end.skawallet.model.Transactions;
import com.ucan.skawallet.back.end.skawallet.repository.DigitalWalletRepository;
import com.ucan.skawallet.back.end.skawallet.repository.TransactionRepository;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService
{

    private final TransactionRepository transactionRepository;
    private final DigitalWalletRepository digitalWalletRepository;
    private final TransactionHistoryService transactionHistoryService;

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

}
