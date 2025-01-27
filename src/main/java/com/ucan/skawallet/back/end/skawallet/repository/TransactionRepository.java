/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.repository;

import com.ucan.skawallet.back.end.skawallet.model.DigitalWallets;
import com.ucan.skawallet.back.end.skawallet.model.Transactions;
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

}
