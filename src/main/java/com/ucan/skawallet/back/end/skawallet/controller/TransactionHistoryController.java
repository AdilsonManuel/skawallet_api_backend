/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.controller;

import com.ucan.skawallet.back.end.skawallet.model.TransactionHistory;
import com.ucan.skawallet.back.end.skawallet.service.TransactionHistoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transaction-history")
@RequiredArgsConstructor
public class TransactionHistoryController
{

    private final TransactionHistoryService service;

    // 1. Endpoint para listar todo o histórico
    @GetMapping("/")
    public ResponseEntity<List<TransactionHistory>> getAllHistory ()
    {
        List<TransactionHistory> history = service.getAllHistory();
        return ResponseEntity.ok(history);
    }

    // 2. Endpoint para buscar histórico por ID da transação
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<TransactionHistory>> getHistoryByTransactionPk (@PathVariable Long transactionId)
    {
        List<TransactionHistory> history = service.getHistoryByTransactionPk(transactionId);
        if (history.isEmpty())
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(history);
    }
    
}
