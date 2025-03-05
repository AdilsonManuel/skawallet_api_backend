/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.repository;

import com.ucan.skawallet.back.end.skawallet.enums.EventType;
import com.ucan.skawallet.back.end.skawallet.model.TransactionHistory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long>
{

    // Consulta por tipo de transação, status e intervalo de datas
    @Query("SELECT th FROM TransactionHistory th WHERE "
            + "(:eventType IS NULL OR th.eventType = :eventType) AND "
            + "(:startDate IS NULL OR th.timestamp >= :startDate) AND "
            + "(:endDate IS NULL OR th.timestamp <= :endDate)")
    List<TransactionHistory> findFilteredHistory (
            @Param("eventType") EventType eventType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Método customizado para buscar histórico por transação
    List<TransactionHistory> findByTransactionPkTransactions (Long transactionId);
}
