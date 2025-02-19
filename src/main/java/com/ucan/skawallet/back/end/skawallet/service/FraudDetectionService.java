/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.model.FraudReport;
import com.ucan.skawallet.back.end.skawallet.model.Transactions;
import com.ucan.skawallet.back.end.skawallet.repository.FraudLogRepository;
import com.ucan.skawallet.back.end.skawallet.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService
{

    private final TransactionRepository transactionRepository;
    private final FraudLogRepository fraudLogRepository;

    public boolean checkForFraud (Transactions transaction)
    {
        Long sourceUserId = transaction.getSourceWallet() != null ? transaction.getSourceWallet().getUser().getPkUsers() : null;
        Long destinationUserId = transaction.getDestinationWallet() != null ? transaction.getDestinationWallet().getUser().getPkUsers() : null;

        log.info("Verificando fraude para usuários: origem={}, destino={}", sourceUserId, destinationUserId);

        // 🚨 1. Detectar transações acima do limite permitido
        if (transaction.getAmount().compareTo(new BigDecimal("50000")) > 0)
        {
            log.warn("🚨 ALERTA: Transação suspeita acima do limite: {}", transaction);
            saveFraudAlert(transaction, "Transação acima do limite permitido.");
            return true;
        }

        // 🚨 2. Detectar múltiplas transações de alto valor em curto intervalo
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(30);
        Long countRecentTransactions = transactionRepository.countHighValueTransactions(sourceUserId, startTime, new BigDecimal("500000"));

        if (countRecentTransactions >= 3)
        {
            log.warn("🚨 ALERTA: Múltiplas transações suspeitas em curto período.");
            saveFraudAlert(transaction, "Usuário realizou várias transações de alto valor em 30 minutos.");
            return true;
        }

        // 🚨 3. Verificar transações incomuns para um usuário específico
        BigDecimal userAvg = transactionRepository.getUserAverageTransactionAmount(sourceUserId);
        if (userAvg != null && transaction.getAmount().compareTo(userAvg.multiply(new BigDecimal("5"))) > 0)
        {
            log.warn("🚨 ALERTA: Transação muito acima da média do usuário.");
            saveFraudAlert(transaction, "O valor da transação é 5x maior que a média histórica do usuário.");
            return true;
        }

        log.info("✅ Nenhuma atividade suspeita detectada.");
        return false;
    }

    private void saveFraudAlert (Transactions transaction, String reason)
    {
        FraudReport fraudReport = new FraudReport();
        fraudReport.setTransaction(transaction);
        fraudReport.setReason(reason);
        fraudReport.setDetectedAt(LocalDateTime.now());

        fraudLogRepository.save(fraudReport);
    }
}
