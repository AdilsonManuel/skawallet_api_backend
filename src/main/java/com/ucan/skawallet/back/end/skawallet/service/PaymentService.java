//package com.ucan.skawallet.back.end.skawallet.service;
//
//import com.ucan.skawallet.back.end.skawallet.dto.PaymentRequestDTO;
//import com.ucan.skawallet.back.end.skawallet.enums.EventType;
//import com.ucan.skawallet.back.end.skawallet.enums.TransactionStatus;
//import com.ucan.skawallet.back.end.skawallet.enums.TransactionType;
//import com.ucan.skawallet.back.end.skawallet.model.DigitalWallets;
//import com.ucan.skawallet.back.end.skawallet.model.Partner;
//import com.ucan.skawallet.back.end.skawallet.model.Transactions;
//import com.ucan.skawallet.back.end.skawallet.repository.DigitalWalletRepository;
//import com.ucan.skawallet.back.end.skawallet.repository.PartnerRepository;
//import com.ucan.skawallet.back.end.skawallet.repository.TransactionRepository;
//import java.math.BigDecimal;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class PaymentService
//{
//
//    private final DigitalWalletRepository walletRepository;
//    private final TransactionRepository transactionRepository;
//    private final PartnerRepository partnerRepository;
//    private final TransactionHistoryService transactionHistoryService;
//
//    public String processPayment (PaymentRequestDTO request)
//    {
//        DigitalWallets sourceWallet = walletRepository.findById(request.getSourceWalletId())
//                .orElseThrow(() -> new RuntimeException("Carteira de origem não encontrada."));
//
//        DigitalWallets destinationWallet = walletRepository.findById(request.getDestinationWalletId())
//                .orElseThrow(() -> new RuntimeException("Carteira de destino não encontrada."));
//
//        if (sourceWallet.getBalance().compareTo(request.getAmount()) < 0)
//        {
//            throw new RuntimeException("Saldo insuficiente.");
//        }
//
//        // Atualizar saldos
//        sourceWallet.setBalance(sourceWallet.getBalance().subtract(request.getAmount()));
//        destinationWallet.setBalance(destinationWallet.getBalance().add(request.getAmount()));
//        walletRepository.save(sourceWallet);
//        walletRepository.save(destinationWallet);
//
//        // Registrar a transação
//        Transactions transaction = new Transactions();
//        transaction.setSourceWallet(sourceWallet);
//        transaction.setDestinationWallet(destinationWallet);
//        transaction.setAmount(request.getAmount());
//        transaction.setTransactionType(TransactionType.PAYMENT);
//        transaction.setStatus(TransactionStatus.COMPLETED);
//        transactionRepository.save(transaction);
//
//        return "Pagamento realizado com sucesso!";
//    }
//
//    public Transactions payToPartner (String walletCode, String partnerCode, BigDecimal amount)
//    {
//
//        // Buscar a carteira pelo código único
//        DigitalWallets wallet = walletRepository.getWalletByCode(walletCode)
//                .orElseThrow(() -> new RuntimeException("Carteira não encontrada"));
//
//        // Buscar o parceiro pelo código único
//        Partner partner = partnerRepository.findByPartnerCode(partnerCode)
//                .orElseThrow(() -> new RuntimeException("Parceiro não encontrado"));
//
//        // Validar se o parceiro aceita pagamento por carteira digital
//        if (!partner.getPaymentSupported())
//        {
//            throw new RuntimeException("Este parceiro não aceita pagamentos via carteira digital.");
//        }
//
//        // Verificar saldo disponível
//        if (wallet.getBalance().compareTo(amount) < 0)
//        {
//            throw new RuntimeException("Saldo insuficiente na carteira.");
//        }
//
//        // Deduzir saldo da carteira
//        wallet.setBalance(wallet.getBalance().subtract(amount));
//        walletRepository.save(wallet);
//
//        // Registrar transação
//        Transactions transaction = new Transactions();
//        transaction.setAmount(amount);
//        transaction.setTransactionType(TransactionType.PAYMENT);
//        transaction.setStatus(TransactionStatus.COMPLETED);
//        transaction.setSourceWallet(wallet);
//        transaction.setDescription("Pagamento para " + partner.getName());
//
//        transactionRepository.save(transaction);
//
//        transactionHistoryService.saveHistory(transaction, EventType.CREATED);
//
//        return transaction;
//    }
//}
