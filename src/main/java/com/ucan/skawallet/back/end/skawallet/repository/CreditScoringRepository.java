/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.repository;

import com.ucan.skawallet.back.end.skawallet.model.CreditScoring;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author azm
 */
@Repository
public interface CreditScoringRepository extends JpaRepository<CreditScoring, Long>
{

    Optional<CreditScoring> findByUserPkUsers (Long userId); // Buscar o scoring de um usuário pelo ID

    boolean existsByUserPkUsers (Long userId); // Verificar se um usuário já possui um score registrado
}
