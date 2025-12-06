package com.ucan.skawallet.back.end.skawallet.controller;

import com.ucan.skawallet.back.end.skawallet.service.ExcelUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
public class DataLoadController {

    private final ExcelUploadService excelUploadService;

    @PostMapping("/load")
    public ResponseEntity<String> loadData() {
        try {
            excelUploadService.loadData();
            return ResponseEntity.ok("Dados carregados com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao carregar dados: " + e.getMessage());
        }
    }

    @PostMapping("/simulate-transactions")
    public ResponseEntity<String> simulateTransactions() {
        try {
            excelUploadService.simulateTransactions();
            return ResponseEntity.ok("Simulação de transações concluída com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao simular transações: " + e.getMessage());
        }
    }

    @PostMapping("/simulate-installments")
    public ResponseEntity<String> simulateInstallments() {
        try {
            excelUploadService.simulateInstallments();
            return ResponseEntity.ok("Simulação de parcelamentos concluída com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao simular parcelamentos: " + e.getMessage());
        }
    }

    @PostMapping("/simulate-topups")
    public ResponseEntity<String> simulateTopUps() {
        try {
            excelUploadService.simulateTopUps();
            return ResponseEntity.ok("Simulação de recargas concluída com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao simular recargas: " + e.getMessage());
        }
    }
}
