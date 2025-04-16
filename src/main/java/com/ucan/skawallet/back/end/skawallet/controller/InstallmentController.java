/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.controller;

import com.ucan.skawallet.back.end.skawallet.dto.InstallmentRequestDTO;
import com.ucan.skawallet.back.end.skawallet.model.Installment;
import com.ucan.skawallet.back.end.skawallet.service.InstallmentService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author azm
 */
@RestController
@RequestMapping("/api/v1/installments")
@RequiredArgsConstructor
public class InstallmentController
{

    private final InstallmentService installmentService;

    @PostMapping("/")
    public ResponseEntity<Installment> createInstallment (@RequestBody InstallmentRequestDTO request)
    {
        return ResponseEntity.ok(installmentService.createInstallment(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Installment>> getUserInstallments (@PathVariable Long userId)
    {
        return ResponseEntity.ok(installmentService.getUserInstallments(userId));
    }

    @PostMapping("/pay")
    public ResponseEntity<String> payInstallment (
            @RequestParam Long installmentId,
            @RequestParam String walletCode,
            @RequestParam BigDecimal amount)
    {
        String result = installmentService.payInstallment(installmentId, walletCode, amount);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/installments")
    public ResponseEntity<List<Installment>> getAllInstallments ()
    {
        List<Installment> installments = installmentService.getAllInstallments();
        return ResponseEntity.ok(installments.isEmpty() ? Collections.emptyList() : installments);
    }
}
