/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.model.FraudReport;
import com.ucan.skawallet.back.end.skawallet.repository.FraudLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FraudReportService
{

    private final FraudLogRepository fraudLogRepository;

    public List<FraudReport> generateFraudReport ()
    {
        return fraudLogRepository.findAll();
    }
}
