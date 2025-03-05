/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.controller;

import com.ucan.skawallet.back.end.skawallet.model.FraudReport;
import com.ucan.skawallet.back.end.skawallet.service.FraudReportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
public class FraudController
{

    private final FraudReportService fraudReportService;

    @GetMapping("/report")
    public ResponseEntity<List<FraudReport>> getFraudReport ()
    {
        return ResponseEntity.ok(fraudReportService.generateFraudReport());
    }
}
