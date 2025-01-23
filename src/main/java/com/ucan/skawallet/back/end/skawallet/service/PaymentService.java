package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.dto.PaymentRequestDTO;
import com.ucan.skawallet.back.end.skawallet.model.DigitalWallets;
import com.ucan.skawallet.back.end.skawallet.model.TransactionStatus;
import com.ucan.skawallet.back.end.skawallet.model.TransactionType;
import com.ucan.skawallet.back.end.skawallet.model.Transactions;
import com.ucan.skawallet.back.end.skawallet.repository.DigitalWalletRepository;
import com.ucan.skawallet.back.end.skawallet.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService
{
	
	private final DigitalWalletRepository walletRepository;
	private final TransactionRepository transactionRepository;
	
	public String processPayment(PaymentRequestDTO request)
	{
		DigitalWallets sourceWallet = walletRepository.findById(request.getSourceWalletId())
				.orElseThrow(() -> new RuntimeException("Carteira de origem não encontrada."));
		
		DigitalWallets destinationWallet = walletRepository.findById(request.getDestinationWalletId())
				.orElseThrow(() -> new RuntimeException("Carteira de destino não encontrada."));
		
		if (sourceWallet.getBalance().compareTo(request.getAmount()) < 0)
		{
			throw new RuntimeException("Saldo insuficiente.");
		}
		
		// Atualizar saldos
		sourceWallet.setBalance(sourceWallet.getBalance().subtract(request.getAmount()));
		destinationWallet.setBalance(destinationWallet.getBalance().add(request.getAmount()));
		walletRepository.save(sourceWallet);
		walletRepository.save(destinationWallet);
		
		// Registrar a transação
		Transactions transaction = new Transactions();
		transaction.setSourceWallet(sourceWallet);
		transaction.setDestinationWallet(destinationWallet);
		transaction.setAmount(request.getAmount());
		transaction.setTransactionType(TransactionType.PAYMENT);
		transaction.setStatus(TransactionStatus.COMPLETED);
		transactionRepository.save(transaction);
		
		return "Pagamento realizado com sucesso!";
	}
}