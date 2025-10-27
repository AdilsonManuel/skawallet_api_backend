/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author azm
 */
@Data
@Builder
public class PartnerResponseDTO
{

    private Long pkPartners;
    private String partnerCode;
    private String name;
    private String description;
    private String category;
    private Boolean paymentSupported;
    private String contactInfo;
    private LocalDateTime createdAt;

    // A lista de produtos agora usa o ProdutoDTO, que é seguro
    private List<ProdutoDTO> produtos;
}
