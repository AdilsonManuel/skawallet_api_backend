/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.model;

import com.ucan.skawallet.back.end.skawallet.enums.TransactionType;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionStatus;
import com.ucan.skawallet.back.end.skawallet.enums.PaymentMethod;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "transactions")
@Data
public class Transactions
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pkTransactions; // pk_transactions

    @Column(nullable = false)
    private BigDecimal amount; // amount

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType; // transaction_type

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING; // status

    private LocalDateTime createdAt = LocalDateTime.now(); // created_at

    private LocalDateTime completedAt; // completed_at

    @ManyToOne
    @JoinColumn(name = "fk_source_wallet", referencedColumnName = "pk_digital_wallets", nullable = true)
    private DigitalWallets sourceWallet; // fk_source_wallet

    @ManyToOne
    @JoinColumn(name = "fk_destination_wallet", referencedColumnName = "pk_digital_wallets", nullable = true)
    private DigitalWallets destinationWallet; // fk_destination_wallet

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod = PaymentMethod.DIGITAL_WALLET; // payment_method

    private String description; // description

}
