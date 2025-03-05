/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.model;

import com.ucan.skawallet.back.end.skawallet.enums.KYCStatus;
import com.ucan.skawallet.back.end.skawallet.enums.UserType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Entity
@Table(name = "users")
public class Users implements UserDetails
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_users")
    private Long pkUsers;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type; // Enum USER, ADMIN, MERCHANT

    @Column(nullable = false)
    private Boolean enabled = true;

    private Boolean locked = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Enumerated(EnumType.STRING)
    private KYCStatus kycStatus = KYCStatus.PENDING; // Padrão: Pendente

    private String idDocument; // Documento de Identificação
    private String selfieUrl;  // URL da Selfie enviada para verificação
    private String verificationCode; // Código para ativação da conta

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities ()
    {

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(name);

        return Collections.singletonList(simpleGrantedAuthority);
    }

    @Override
    public String getUsername ()
    {
        return name;
    }

    @Override
    public boolean isAccountNonExpired ()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked ()
    {
        return locked == null;
    }

    @Override
    public boolean isCredentialsNonExpired ()
    {
        return true;
    }

    @Override
    public boolean isEnabled ()
    {
        return enabled;
    }

}
