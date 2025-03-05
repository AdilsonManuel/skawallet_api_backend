/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.controller;

import com.ucan.skawallet.back.end.skawallet.model.Installment;
import com.ucan.skawallet.back.end.skawallet.service.InstallmentService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author AdilsonManuel
 */
@RestController
@RequestMapping("/api/v1/installments")
@RequiredArgsConstructor
public class InstallmentController
{

    private final InstallmentService installmentService;

    @PostMapping("/create")
    public ResponseEntity<List<Installment>> createInstallments(
            @RequestParam Long transactionId,
            @RequestParam int installmentsCount,
            @RequestParam BigDecimal interestRate)
    {

        List<Installment> installments = installmentService.createInstallments(transactionId, installmentsCount, interestRate);
        return ResponseEntity.ok(installments);
    }

    @PostMapping("/pay/{installmentId}")
    public ResponseEntity<String> payInstallment(@PathVariable Long installmentId)
    {
        installmentService.payInstallment(installmentId);
        return ResponseEntity.ok("Parcela paga com sucesso.");
    }
}
