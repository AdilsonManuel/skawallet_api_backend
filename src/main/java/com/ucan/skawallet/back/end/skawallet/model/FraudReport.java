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
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "fraud_reports")
@Data
public class FraudReport
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pkFraudReport;

    @OneToOne
    @JoinColumn(name = "fk_transactions", nullable = false)
    private Transactions transaction;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime detectedAt;
}
