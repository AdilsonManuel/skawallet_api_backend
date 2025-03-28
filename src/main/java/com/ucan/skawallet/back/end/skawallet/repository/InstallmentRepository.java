/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.repository;

import com.ucan.skawallet.back.end.skawallet.enums.InstallmentStatus;
import com.ucan.skawallet.back.end.skawallet.model.Installment;
import com.ucan.skawallet.back.end.skawallet.model.Users;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author azm
 */
@Repository
public interface InstallmentRepository extends JpaRepository<Installment, Long>
{

    List<Installment> findByUserPkUsers (Long userId); // Buscar parcelas por usuário

//    List<Installment> findByTransactionPkTransactions (Long transactionId); // Buscar parcelas por transação

    List<Installment> findByStatus (InstallmentStatus status); // Buscar parcelas por status

//    public List<Installment> findByDueDateBetweenAndStatus (LocalDateTime threeDaysAhead, LocalDateTime oneDayAhead, InstallmentStatus installmentStatus);

    public int countLatePaymentsByUser (Users user);
}
