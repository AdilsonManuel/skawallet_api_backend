/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.controller;

import com.ucan.skawallet.back.end.skawallet.enums.KYCStatus;
import com.ucan.skawallet.back.end.skawallet.service.KYCService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
public class KYCController
{

    private final KYCService kycService;

    @PutMapping("/verify/{userId}")
    public ResponseEntity<String> verifyUser (@PathVariable Long userId)
    {
        kycService.updateKYCStatus(userId, KYCStatus.VERIFIED);
        return ResponseEntity.ok("Usuário verificado com sucesso!");
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<Boolean> checkKYCStatus (@PathVariable Long userId)
    {
        return ResponseEntity.ok(kycService.isUserVerified(userId));
    }
}
