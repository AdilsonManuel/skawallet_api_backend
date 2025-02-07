/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import com.ucan.skawallet.back.end.skawallet.dto.PaymentRequestDTO;
import com.ucan.skawallet.back.end.skawallet.dto.PaymentResponse;
import com.ucan.skawallet.back.end.skawallet.repository.PaymentProvider;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author azm
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentStripeService implements PaymentProvider
{

    @Value("${stripe.api-key}")
    private String apiKey;

    @PostConstruct
    public void init ()
    {
        Stripe.apiKey = apiKey; // Inicializa o Stripe com a chave da API
    }

    @Override
    public PaymentResponse initiatePayment (PaymentRequestDTO request)
    {
        try
        {
            // Criação de um PaymentIntent no Stripe
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(request.getAmount().longValue())
                    .setCurrency(request.getCurrency())
                    .setPaymentMethod(request.getPaymentMethodId())
                    .setDescription(request.getDescription())
                    .setConfirm(true) // Confirma o pagamento automaticamente
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            log.info("Pagamento iniciado com sucesso no Stripe: {}", paymentIntent.getId());

            return new PaymentResponse("succeeded", paymentIntent.getId(), "Pagamento realizado com sucesso!");
        }
        catch (StripeException e)
        {
            log.error("Erro ao iniciar pagamento no Stripe: {}", e.getMessage());
            throw new RuntimeException("Erro ao iniciar pagamento: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponse checkPaymentStatus (String paymentIntentId)
    {
        try
        {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            log.info("Status do pagamento recuperado no Stripe: {}", paymentIntent.getStatus());

            return new PaymentResponse(
                    paymentIntent.getCurrency(),
                    BigDecimal.valueOf(paymentIntent.getAmount()), // Convertendo Long para BigDecimal
                    paymentIntent.getStatus(),
                    paymentIntent.getId(),
                    "Status do pagamento: " + paymentIntent.getStatus(),
                    paymentIntent.getCustomer()
            );
        }
        catch (StripeException e)
        {
            log.error("Erro ao consultar status do pagamento no Stripe: {}", e.getMessage());
            throw new RuntimeException("Erro ao consultar status do pagamento: " + e.getMessage());
        }
    }

    @Override
    public String createProduct (String productName, String productDescription, BigDecimal price, String currency, String interval)
    {
        try
        {
            // Criação do produto
            ProductCreateParams productParams = ProductCreateParams.builder()
                    .setName(productName)
                    .setDescription(productDescription)
                    .build();

            Product product = Product.create(productParams);

            // Criação do plano associado ao produto
            PriceCreateParams priceParams = PriceCreateParams.builder()
                    .setUnitAmount(price.multiply(BigDecimal.valueOf(100)).longValue()) // Valor em centavos
                    .setCurrency(currency)
                    .setRecurring(PriceCreateParams.Recurring.builder()
                            .setInterval(convertToInterval(interval)) // Conversão de String para Interval
                            .build())
                    .setProduct(product.getId())
                    .build();

            Price stripePrice = Price.create(priceParams);

            log.info("Produto criado com sucesso no Stripe: {}", product.getId());
            return "Produto criado com sucesso! ID do produto: " + product.getId() + ", ID do preço: " + stripePrice.getId();
        }
        catch (StripeException e)
        {
            log.error("Erro ao criar produto no Stripe: {}", e.getMessage());
            throw new RuntimeException("Erro ao criar produto: " + e.getMessage());
        }
    }

    private PriceCreateParams.Recurring.Interval convertToInterval (String interval)
    {
        if ("month".equalsIgnoreCase(interval))
        {
            return PriceCreateParams.Recurring.Interval.MONTH;
        }
        else if ("year".equalsIgnoreCase(interval))
        {
            return PriceCreateParams.Recurring.Interval.YEAR;
        }
        else
        {
            throw new IllegalArgumentException("Intervalo inválido: " + interval + ". Use 'month' ou 'year'.");
        }
    }

}
