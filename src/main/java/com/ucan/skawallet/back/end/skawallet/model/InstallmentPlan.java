/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "installment_plans")
@Data
public class InstallmentPlan
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pkInstallmentPlan;

    @ManyToOne
    @JoinColumn(name = "fk_users", nullable = false)
    private Users user;

    @Column(nullable = false)
    private int maxInstallments; // Número máximo de parcelas permitidas

    @Column(nullable = false)
    private BigDecimal interestRate = BigDecimal.ZERO; // Taxa de juros

    @Column(nullable = false)
    private BigDecimal minTransactionAmount = BigDecimal.valueOf(5000); // Valor mínimo para parcelar

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
