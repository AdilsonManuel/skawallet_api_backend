/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.repository;

import com.ucan.skawallet.back.end.skawallet.model.TopUpReference;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author azm
 */
@Repository
public interface TopUpReferenceRepository extends JpaRepository<TopUpReference, Long>
{

    Optional<TopUpReference> findByReferenceCode (String referenceCode);

    boolean existsByReferenceCode (String referenceCode);

    @Query("SELECT r FROM TopUpReference r WHERE r.wallet.user.id = :userId AND r.status = 'COMPLETED'")
    List<TopUpReference> findConfirmedByUserId (@Param("userId") Long userId);

}
