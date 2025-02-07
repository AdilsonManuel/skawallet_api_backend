/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.repository;

import com.ucan.skawallet.back.end.skawallet.dto.PaymentRequestDTO;
import com.ucan.skawallet.back.end.skawallet.dto.PaymentResponse;
import java.math.BigDecimal;

/**
 *
 * @author azm
 */
public interface PaymentProvider
{

    PaymentResponse initiatePayment (PaymentRequestDTO request);

    PaymentResponse checkPaymentStatus (String paymentIntentId);

    String createProduct (String productName, String productDescription, BigDecimal price, String currency, String interval);
}
