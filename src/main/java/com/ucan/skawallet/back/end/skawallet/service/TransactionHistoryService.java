/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.model.EventType;
import com.ucan.skawallet.back.end.skawallet.model.TransactionHistory;
import com.ucan.skawallet.back.end.skawallet.model.Transactions;
import com.ucan.skawallet.back.end.skawallet.repository.TransactionHistoryRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService
{

    private final TransactionHistoryRepository repository;

    public void save (TransactionHistory history)
    {
        repository.save(history);
    }

    // 1. Listar todo o histórico de transações
    public List<TransactionHistory> getAllHistory ()
    {
        return repository.findAll();
    }

    // 2. Buscar histórico por ID da transação
    public List<TransactionHistory> getHistoryByTransactionPk (Long transactionId)
    {
        return repository.findByTransactionPkTransactions(transactionId);
    }

    public void saveHistory (Transactions transaction, EventType eventType)
    {
        System.err.println("com.ucan.skawallet.back.end.skawallet.service.TransactionHistoryService.saveHistory()" + transaction);
        TransactionHistory history = TransactionHistory.builder()
                .transaction(transaction)
                .eventType(eventType)
                .timestamp(LocalDateTime.now())
                .build();

        repository.save(history);
    }
}
