/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.repository;

import com.ucan.skawallet.back.end.skawallet.enums.TransactionStatus;
import com.ucan.skawallet.back.end.skawallet.model.DigitalWallets;
import com.ucan.skawallet.back.end.skawallet.model.Transactions;
import com.ucan.skawallet.back.end.skawallet.model.Users;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Long>
{

    List<Transactions> findBySourceWalletOrDestinationWallet (DigitalWallets sourceWallet, DigitalWallets destinationWallet);

    @Query(value = """
         SELECT 
                            t.pk_transactions AS transactionId,
                            t.amount AS amount,
                            t.transaction_type AS transactionType,
                            t.status AS status,
                            t.created_at AS createdAt,
                            t.completed_at AS completedAt,
                            sw.wallet_name AS sourceWalletName,
                            dw.wallet_name AS destinationWalletName,
                            t.payment_method AS paymentMethod,
                            t.description AS description
                        FROM 
                            transactions t
                        LEFT JOIN 
                            digital_wallets sw ON t.fk_source_wallet = sw.pk_digital_wallets
                        LEFT JOIN 
                            digital_wallets dw ON t.fk_destination_wallet = dw.pk_digital_wallets
                        WHERE 
                            sw.fk_users = :userId OR dw.fk_users = :userId
                        ORDER BY 
                            t.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findTransactionsByUserId (@Param("userId") Long userId);

    @Query("SELECT t FROM Transactions t "
            + "WHERE (t.sourceWallet.user.pkUsers = :userId OR t.destinationWallet.user.pkUsers = :userId) "
            + "AND t.createdAt BETWEEN :startDate AND :endDate "
            + "ORDER BY t.createdAt DESC")
    List<Transactions> findTransactionsByUserAndDateRange (
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Contar transações de alto valor feitas nos últimos 30 minutos
    @Query("SELECT COUNT(t) FROM Transactions t WHERE t.sourceWallet.user.pkUsers = :userId AND t.createdAt >= :startTime AND t.amount >= :threshold")
    Long countHighValueTransactions (@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("threshold") BigDecimal threshold);

    // Média de valores das transações do usuário
    @Query("SELECT COALESCE(AVG(t.amount), 0) FROM Transactions t WHERE t.sourceWallet.user.pkUsers = :userId")
    BigDecimal getUserAverageTransactionAmount (@Param("userId") Long userId);

    int countBySourceWallet_UserOrDestinationWallet_UserAndStatus (Users user1, Users user2, TransactionStatus status);

    @Query(value = """
        SELECT COUNT(*) 
        FROM transactions t
        JOIN digital_wallets dw_source ON t.fk_source_wallet = dw_source.pk_digital_wallets
        LEFT JOIN digital_wallets dw_dest ON t.fk_destination_wallet = dw_dest.pk_digital_wallets
        WHERE dw_source.fk_users = :userId OR dw_dest.fk_users = :userId
    """, nativeQuery = true)
    long countTransactionsByUserId (@Param("userId") Long userId);

    @Query("""
    SELECT t FROM Transactions t
    WHERE t.sourceWallet.user.id = :userId
       OR t.destinationWallet.user.id = :userId
""")
    List<Transactions> findByUserId (@Param("userId") Long userId);

    List<Transactions> findBySourceWallet (DigitalWallets wallet); // Transações onde a carteira é origem

    List<Transactions> findByDestinationWallet (DigitalWallets wallet); // Transações onde a carteira é destino

}
