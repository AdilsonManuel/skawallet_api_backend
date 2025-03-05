/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.repository;

import com.ucan.skawallet.back.end.skawallet.model.Installment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author AdilsonManuel
 */
public interface InstallmentRepository extends JpaRepository<Installment, Long>
{

}
