/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.controller;

import com.ucan.skawallet.back.end.skawallet.dto.PaymentRequestDTO;
import com.ucan.skawallet.back.end.skawallet.dto.PaymentResponse;
import com.ucan.skawallet.back.end.skawallet.dto.ProductRequestDTO;
import com.ucan.skawallet.back.end.skawallet.service.PaymentStripeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author azm
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentStripeController
{

    private final PaymentStripeService paymentProvider;

    @PostMapping("/")
    public ResponseEntity<PaymentResponse> initiatePayment (@RequestBody PaymentRequestDTO request)
    {
        log.info("Recebida solicitação de pagamento: {}", request);
        PaymentResponse response = paymentProvider.initiatePayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentIntentId}")
    public ResponseEntity<PaymentResponse> checkPaymentStatus (@PathVariable String paymentIntentId)
    {
        log.info("Consultando status do pagamento: {}", paymentIntentId);
        PaymentResponse response = paymentProvider.checkPaymentStatus(paymentIntentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/products")
    @PreAuthorize("hasRole('ADMIN')") // Apenas administradores podem criar produtos
    public ResponseEntity<String> createProduct (@RequestBody ProductRequestDTO request)
    {
        log.info("Tentativa de criar produto no Stripe: {}", request);
        String result = paymentProvider.createProduct(
                request.getProductName(),
                request.getProductDescription(),
                request.getPrice(),
                request.getCurrency(),
                request.getFrequency()
        );
        return ResponseEntity.ok(result);
    }
}
