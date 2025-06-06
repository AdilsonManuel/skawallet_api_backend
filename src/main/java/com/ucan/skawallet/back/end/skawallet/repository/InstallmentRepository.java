/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.repository;

import com.ucan.skawallet.back.end.skawallet.enums.InstallmentStatus;
import com.ucan.skawallet.back.end.skawallet.model.Installment;
import com.ucan.skawallet.back.end.skawallet.model.Users;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT i FROM Installment i WHERE i.nextDueDate < :hoje AND i.status <> 'COMPLETED'")
    List<Installment> findOverdueInstallments (@Param("hoje") LocalDate hoje);

    @Query("SELECT i FROM Installment i WHERE i.user = :user AND i.status = :status")
    List<Installment> findByUserAndStatus (@Param("user") Users user, @Param("status") InstallmentStatus status);

    @Query("SELECT i FROM Installment i "
            + "LEFT JOIN FETCH i.partner "
            + "LEFT JOIN FETCH i.produto "
            + "WHERE i.user.id = :userId "
            + "ORDER BY i.nextDueDate ASC")
    List<Installment> findInstallmentsByUserId (@Param("userId") Long userId);

}
