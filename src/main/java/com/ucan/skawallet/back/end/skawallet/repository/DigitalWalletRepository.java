/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.repository;

import com.ucan.skawallet.back.end.skawallet.model.DigitalWallets;
import com.ucan.skawallet.back.end.skawallet.model.Users;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DigitalWalletRepository extends JpaRepository<DigitalWallets, Long>
{

    List<DigitalWallets> findByUser (Users user);

    /**
     * Busca uma carteira específica de um usuário.
     *
     * @param walletId ID da carteira.
     * @param userId ID do usuário.
     * @return Carteira, se encontrada.
     */
    @Query(value = "SELECT dw FROM digital_wallets dw WHERE dw.fk_users = ? AND dw.pk_digital_wallets = ?", nativeQuery = true)
    Optional<DigitalWallets> findByIdAndUserId (@Param("walletId") Long walletId, @Param("userId") Long userId);

    @Query(
            value = "SELECT * FROM digital_wallets WHERE wallet_code = :code",
            nativeQuery = true
    )
    Optional<DigitalWallets> getWalletByCode (@Param("code") String code);

    @Query(value = "SELECT * FROM digital_wallets WHERE fk_users = :userId ORDER BY created_at DESC LIMIT 1",
            nativeQuery = true)
    Optional<DigitalWallets> findByUserId (@Param("userId") Long userId);
}
