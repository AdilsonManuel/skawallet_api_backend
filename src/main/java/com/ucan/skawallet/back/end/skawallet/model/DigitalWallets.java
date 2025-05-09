/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ucan.skawallet.back.end.skawallet.enums.WalletType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "digital_wallets")
@Data
public class DigitalWallets
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_digital_wallets")
    private Long pk_digital_wallets;

    @Column(nullable = false)
    private String walletName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletType walletType; // Enum PERSONAL, MERCHANT, SAVINGS

    @Column(nullable = false, unique = true, updatable = false)
    private String walletCode; // Código único da carteira

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private String currency = "AKZ";

    @ManyToOne
    @JoinColumn(name = "fk_users", nullable = false)
    @JsonIgnore
    private Users user;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private Boolean isDefault = false;

    @PrePersist
    public void generateWalletCode ()
    {
        if (this.walletCode == null)
        {
            this.walletCode = generateUniqueCode();
        }
    }

    private String generateUniqueCode ()
    {
        return "SKA-" + user.getPkUsers() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
