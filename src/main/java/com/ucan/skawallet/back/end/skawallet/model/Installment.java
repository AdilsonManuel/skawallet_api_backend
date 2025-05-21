/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.model;

import com.ucan.skawallet.back.end.skawallet.enums.InstallmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "installments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Installment
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long installmentId;

    @ManyToOne
    @JoinColumn(name = "fk_users", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "fk_partner", nullable = false)
    private Partner partner;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private int installments; // Número de parcelas

    @Column(nullable = false)
    private int remainingInstallments; // Parcelas restantes

    @Column(nullable = false)
    private BigDecimal monthlyPayment; // Valor de cada parcela

    @Column(nullable = false)
    private LocalDate nextDueDate; // Próxima data de vencimento

    @Enumerated(EnumType.STRING)
    private InstallmentStatus status = InstallmentStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_product")
    private Produto produto;
}
