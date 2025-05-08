/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/SpringFramework/Controller.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.controller;

import com.ucan.skawallet.back.end.skawallet.dto.DepositRequestDTO;
import com.ucan.skawallet.back.end.skawallet.dto.TransactionDTO;
import com.ucan.skawallet.back.end.skawallet.dto.TransactionResponseDTO;
import com.ucan.skawallet.back.end.skawallet.dto.UserHistoryDTO;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionStatus;
import com.ucan.skawallet.back.end.skawallet.model.Transactions;
import com.ucan.skawallet.back.end.skawallet.service.TransactionService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController
{

    private final TransactionService transactionService;

    @GetMapping("/")
    public ResponseEntity<List<Transactions>> getAllUsers ()
    {
        List<Transactions> transactionses = transactionService.ListTransactions ();

        return new ResponseEntity<> (transactionses, HttpStatus.OK);
    }

    // Criar uma nova transação
    @PostMapping("/")
    public ResponseEntity<Transactions> createTransaction (@RequestBody TransactionDTO transactionDTO)
    {
        Transactions transaction = transactionService.createTransaction (transactionDTO);
        return ResponseEntity.status (HttpStatus.CREATED).body (transaction);
    }

    // Obter todas as transações por carteira
    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<Transactions>> getTransactionsByWallet (@PathVariable Long walletId)
    {
        List<Transactions> transactions = transactionService.getTransactionsByWallet (walletId);
        return ResponseEntity.ok (transactions);
    }

    // Obter detalhes de uma transação específica
    @GetMapping("/{transactionId}")
    public ResponseEntity<Transactions> getTransactionById (@PathVariable Long transactionId)
    {
        Transactions transaction = transactionService.getTransactionById (transactionId);
        return ResponseEntity.ok (transaction);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<TransactionResponseDTO>> getUserTransactionHistory (@PathVariable Long userId)
    {
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByUserId (userId);
        return ResponseEntity.ok (transactions);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Transactions> updateTransaction (
            @PathVariable Long id, @RequestBody Transactions transaction)
    {
        transaction.setPkTransactions (id);
        Transactions updatedTransaction = transactionService.updateTransaction (transaction);
        return ResponseEntity.ok (updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction (@PathVariable Long id)
    {
        transactionService.deleteTransaction (id);
        return ResponseEntity.noContent ().build ();
    }

    @GetMapping("/transaction-history/{userId}")
    public ResponseEntity<List<Transactions>> getTransactionHistoryByDateRange (
            @PathVariable Long userId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate)
    {

        List<Transactions> transactions = transactionService.getTransactionsByUserAndDateRange (userId, startDate, endDate);
        return ResponseEntity.ok (transactions);
    }

    @PatchMapping("/{transactionId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Transactions> updateTransactionStatus (
            @PathVariable Long transactionId,
            @RequestParam TransactionStatus newStatus)
    {

        Transactions updatedTransaction = transactionService.updateTransactionStatus (transactionId, newStatus);
        return ResponseEntity.ok (updatedTransaction);
    }

    @PostMapping("/{walletCode}/withdrawal")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> generateWithdrawalCode (
            @PathVariable String walletCode,
            @RequestParam BigDecimal amount)
    {

        String withdrawalCode = transactionService.generateWithdrawalCode (walletCode, amount);
        return ResponseEntity.ok ("Código de levantamento: " + withdrawalCode);
    }

    @PostMapping("/{walletCode}/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Transactions> transferFunds (
            @PathVariable String walletCode,
            @RequestParam String destinationWalletCode,
            @RequestParam BigDecimal amount)
    {

        Transactions transaction = transactionService.transferFunds (walletCode, destinationWalletCode, amount);
        return ResponseEntity.ok (transaction);
    }

    @PostMapping("/top-up")
    public ResponseEntity<Transactions> topUpWallet (@Valid @RequestBody DepositRequestDTO request)
    {
        Transactions transaction = transactionService.topUpWallet (request);
        return ResponseEntity.ok (transaction);
    }

    @GetMapping("/history/unified/{userId}")
    public ResponseEntity<List<UserHistoryDTO>> getUnifiedHistory (@PathVariable Long userId)
    {
        List<UserHistoryDTO> history = transactionService.getUnifiedUserHistory (userId);
        return ResponseEntity.ok (history);
    }

}
