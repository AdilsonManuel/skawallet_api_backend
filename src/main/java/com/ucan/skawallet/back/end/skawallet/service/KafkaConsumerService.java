///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.ucan.skawallet.back.end.skawallet.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ucan.skawallet.back.end.skawallet.model.TransactionHistory;
//import com.ucan.skawallet.back.end.skawallet.repository.TransactionHistoryRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
///**
// *
// * @author azm
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class KafkaConsumerService
//{
//
//    private final TransactionHistoryRepository transactionHistoryRepository;
//    private final ObjectMapper objectMapper;
//
//    @KafkaListener(topics = "transaction-history", groupId = "skawallet-group")
//    public void consumeTransactionHistory (String message)
//    {
//        log.info("Mensagem consumida do tópico 'transaction-history': {}", message);
//
//        try
//        {
//            // Converter JSON para Objeto TransactionHistory
//            TransactionHistory transactionHistory = objectMapper.readValue(message, TransactionHistory.class);
//            transactionHistoryRepository.save(transactionHistory);
//            log.info("Histórico de transação salvo no banco de dados.");
//        }
//        catch (Exception e)
//        {
//            log.error("Erro ao processar mensagem Kafka: {}", e.getMessage());
//        }
//    }
//}
