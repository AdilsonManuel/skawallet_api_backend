///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.ucan.skawallet.back.end.skawallet.security.config;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ucan.skawallet.back.end.skawallet.model.TransactionHistory;
//import com.ucan.skawallet.back.end.skawallet.model.Transactions;
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
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class TransactionConsumer
//{
//
//    private final TransactionHistoryRepository transactionHistoryRepository;
//    private final ObjectMapper objectMapper;
//
//    @KafkaListener(topics = "transaction-history", groupId = "skawallet-group")
//    public void consumeTransactionHistory (String message)
//    {
//        log.info("Mensagem consumida do Kafka: {}", message);
//
//        try
//        {
//            Transactions transaction = objectMapper.readValue(message, Transactions.class);
//            TransactionHistory history = new TransactionHistory(transaction);
//            transactionHistoryRepository.save(history);
//            log.info("Histórico de transação salvo no banco: {}", history);
//        }
//        catch (JsonProcessingException e)
//        {
//            log.error("Erro ao processar mensagem do Kafka: {}", e.getMessage());
//        }
//    }
//}
