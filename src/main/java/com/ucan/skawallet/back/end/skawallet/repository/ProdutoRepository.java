/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.repository;

import com.ucan.skawallet.back.end.skawallet.model.Produto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author azm
 */
// ProdutoRepository.java
public interface ProdutoRepository extends JpaRepository<Produto, Long>
{

    @Query("SELECT p FROM Produto p WHERE p.partner.pkPartners = :partnerId")
    List<Produto> findByPartnerId (@Param("partnerId") Long partnerId);

}
