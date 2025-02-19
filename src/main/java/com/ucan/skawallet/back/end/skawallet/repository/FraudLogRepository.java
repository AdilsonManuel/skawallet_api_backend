/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.repository;

import com.ucan.skawallet.back.end.skawallet.model.FraudReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author azm
 */
@Repository
public interface FraudLogRepository extends JpaRepository<FraudReport, Long>
{

//    @Query("SELECT t FROM Transactions t WHERE t.user.id = :userId")
//    List<Transactions> findTransactionsByUserId (@Param("userId") Long userId);
}
