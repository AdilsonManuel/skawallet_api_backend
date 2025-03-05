///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.ucan.skawallet.back.end.skawallet.security.config;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ucan.skawallet.back.end.skawallet.model.TransactionHistory;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
///**
// *
// * @author azm
// */
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class TransactionProducer
//{
//
////    private final KafkaTemplate<String, String> kafkaTemplate;
//    private final ObjectMapper objectMapper;
//
//    public void sendTransactionEvent (TransactionHistory transaction)
//    {
//        try
//        {
//            String transactionJson = objectMapper.writeValueAsString(transaction);
//            kafkaTemplate.send("transaction-history", transactionJson);
//            log.info("Evento de transação enviado: {}", transactionJson);
//        }
//        catch (JsonProcessingException e)
//        {
//            log.error("Erro ao converter transação para JSON: {}", e.getMessage());
//        }
//    }
//}
