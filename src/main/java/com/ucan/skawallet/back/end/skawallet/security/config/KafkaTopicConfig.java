///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.ucan.skawallet.back.end.skawallet.security.config;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.TopicBuilder;
//
///**
// *
// * @author azm
// */
//@Configuration
//public class KafkaTopicConfig
//{
//
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Bean
//    public NewTopic transactionHistoryTopic ()
//    {
//        return TopicBuilder.name("transaction-history")
//                .partitions(3)
//                .replicas(1)
//                .build();
//    }
//}
