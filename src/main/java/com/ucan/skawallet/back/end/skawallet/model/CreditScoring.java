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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author azm
 */
@Entity
@Table(name = "credit_scorings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditScoring
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_credit_scoring")
    private Long pkCreditScoring;

    @OneToOne
    @JoinColumn(name = "fk_users", nullable = false, unique = true)
    private Users user;

    @Column(nullable = false)
    private Integer score; // Pontuação do usuário (0 a 1000)

    @Column(nullable = false)
    private BigDecimal creditLimit; // Limite de crédito aprovado

    @Column(nullable = false)
    private Integer missedPayments; // Número de pagamentos atrasados

    @Column(nullable = false)
    private Integer successfulPayments; // Número de pagamentos concluídos

    @Column(nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now(); // Última atualização do scoring
}
