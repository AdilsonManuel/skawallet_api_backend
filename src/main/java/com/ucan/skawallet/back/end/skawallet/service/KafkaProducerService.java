///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.ucan.skawallet.back.end.skawallet.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
///**
// *
// * @author azm
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class KafkaProducerService
//{
//
//    private final KafkaTemplate<String, String> kafkaTemplate;
//
//    public void sendTransactionEvent (String message)
//    {
//        log.info("Enviando mensagem para o Kafka: {}", message);
//        kafkaTemplate.send("transaction-history", message);
//    }
//}
