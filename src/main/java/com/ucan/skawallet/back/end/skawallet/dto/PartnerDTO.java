/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.dto;

import com.ucan.skawallet.back.end.skawallet.enums.PartnerCategory;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author azm
 */
@Data
public class PartnerDTO
{

    private Long pkPartners;
    private String partnerCode; // "UNITEL"
    private String name;
    private String description;
    private PartnerCategory category; // Enum: TELECOM, ENERGY, GOV, FINANCE, RETAIL
    private Boolean paymentSupported;
    private String contactInfo;
    private LocalDateTime createdAt;
}
