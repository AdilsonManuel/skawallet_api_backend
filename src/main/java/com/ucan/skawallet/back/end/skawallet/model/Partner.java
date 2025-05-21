/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.model;

import com.ucan.skawallet.back.end.skawallet.enums.PartnerCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 *
 * @author azm
 */
@Entity
@Table(name = "partners")
@Data
public class Partner
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pkPartners;

    @Column(nullable = false, unique = true)
    private String partnerCode; // "UNITEL"

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerCategory category; // Enum: TELECOM, ENERGY, GOV, FINANCE, RETAIL

    @Column(nullable = false)
    private Boolean paymentSupported = true;

    private String contactInfo;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Alteração no Partner.java
    @OneToMany(mappedBy = "partner")
    private List<Produto> produtos;
}
