/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.controller;

import com.ucan.skawallet.back.end.skawallet.model.TopUpReference;
import com.ucan.skawallet.back.end.skawallet.model.TopUpRequest;
import com.ucan.skawallet.back.end.skawallet.service.TopUpReferenceService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/topup")
@RequiredArgsConstructor
public class TopUpReferenceController
{

    private final TopUpReferenceService topUpReferenceService;

    /**
     * Gera uma nova referência de recarga para um usuário.
     *
     * @param userId ID do usuário
     * @param amount Valor da recarga
     * @return A referência gerada
     */
    @PostMapping("/generate")
    public ResponseEntity<TopUpReference> generateTopUpReference (@RequestBody TopUpRequest request)
    {
        TopUpReference reference = topUpReferenceService.generateReference(
                request.getWalletCode(),
                request.getAmount()
        );
        return ResponseEntity.ok(reference);
    }

    /**
     * Confirma uma referência de recarga (caso ainda esteja válida e pendente).
     *
     * @param referenceCode Código da referência
     * @return A referência atualizada com status COMPLETED
     */
    @PostMapping("/confirm")

    public ResponseEntity<TopUpReference> confirm (@RequestBody Map<String, String> request)
    {
        String referenceCode = request.get("referenceCode");
        return ResponseEntity.ok(topUpReferenceService.confirmTopUp(referenceCode));
    }
}
