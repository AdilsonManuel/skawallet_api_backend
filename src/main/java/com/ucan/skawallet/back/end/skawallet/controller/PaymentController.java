///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.ucan.skawallet.back.end.skawallet.controller;
//
//import com.ucan.skawallet.back.end.skawallet.model.Transactions;
//import com.ucan.skawallet.back.end.skawallet.service.PaymentService;
//import java.math.BigDecimal;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/v1/payments")
//@RequiredArgsConstructor
//public class PaymentController
//{
//
//    private final PaymentService paymentService;
//
//    @PostMapping("/pay")
//    public ResponseEntity<Transactions> payToPartner (@RequestParam String walletCode,
//                                                      @RequestParam String partnerCode,
//                                                      @RequestParam BigDecimal amount)
//    {
//        return ResponseEntity.ok(paymentService.payToPartner(walletCode, partnerCode, amount));
//    }
//}
