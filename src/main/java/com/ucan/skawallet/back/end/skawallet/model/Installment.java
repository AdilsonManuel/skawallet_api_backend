/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.model;

import com.ucan.skawallet.back.end.skawallet.enums.InstallmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author AdilsonManuel
 */
@Entity
@Table(name = "installments")
@Data
public class Installment
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pkInstallment;

    @ManyToOne
    @JoinColumn(name = "fk_transaction", nullable = false)
    private Transactions transaction;

    @ManyToOne
    @JoinColumn(name = "fk_users", nullable = false)
    private Users user;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private InstallmentStatus status = InstallmentStatus.PENDING; // PENDENTE, PAGO, ATRASADO

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
