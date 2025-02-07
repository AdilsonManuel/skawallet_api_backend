package com.ucan.skawallet.back.end.skawallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO
{

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal amount;

    @NotBlank(message = "A moeda é obrigatória")
    private String currency;

    @NotBlank(message = "O destinatário é obrigatório")
    private String paymentMethodId;  // Método de pagamento (Ex: "pm_card_visa")
    private String description;      // Descrição da transação
}
