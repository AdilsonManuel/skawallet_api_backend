/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.model;

import com.ucan.skawallet.back.end.skawallet.enums.PaymentStatus;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author azm
 */
@Entity
@Table(name = "installment_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentPayment
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Chave primária do pagamento da parcela

    @ManyToOne
    @JoinColumn(name = "fk_installment", nullable = false)
    private Installment installment; // Referência à parcela paga

    @ManyToOne
    @JoinColumn(name = "fk_digital_wallet", nullable = false)
    private DigitalWallets wallet; // Carteira usada para o pagamento

    @Column(nullable = false)
    private BigDecimal amount; // Valor pago

    @Column(nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now(); // Data do pagamento

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING; // Status do pagamento
}
