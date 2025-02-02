/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.dto.PartnerDTO;
import com.ucan.skawallet.back.end.skawallet.model.Partner;
import com.ucan.skawallet.back.end.skawallet.repository.PartnerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author azm
 */
@Service
@RequiredArgsConstructor
public class PartnerService
{

    private final PartnerRepository partnerRepository;

    // Criar um novo parceiro
    public Partner createPartner (Partner partner)
    {
        partner.setPartnerCode(generatePartnerCode(partner.getName()));
        return partnerRepository.save(partner);
    }

    // Buscar todos os parceiros
    public List<Partner> getAllPartners ()
    {
        return partnerRepository.findAll();
    }

    // Buscar parceiro pelo código único
    public Partner getPartnerByCode (String partnerCode)
    {
        return partnerRepository.findByPartnerCode(partnerCode)
                .orElseThrow(() -> new RuntimeException("Parceiro não encontrado."));
    }

    // Gerar código único baseado no nome
    private String generatePartnerCode (String name)
    {
        return name.toUpperCase().replaceAll("\\s+", "_").substring(0, Math.min(name.length(), 10));
    }

    // Atualizar parcialmente um parceiro
    public Partner updatePartner (String partnerCode, PartnerDTO partnerDTO)
    {
        Partner partner = getPartnerByCode(partnerCode);

        if (partnerDTO.getName() != null)
        {
            partner.setName(partnerDTO.getName());
        }
        if (partnerDTO.getContactInfo() != null)
        {
            partner.setContactInfo(partnerDTO.getContactInfo());
        }
        if (partnerDTO.getDescription() != null)
        {
            partner.setDescription(partnerDTO.getDescription());
        }
        if (partnerDTO.getCategory() != null)
        {
            partner.setCategory(partnerDTO.getCategory());
        }
        if (partnerDTO.getPaymentSupported() != null)
        {
            partner.setPaymentSupported(partnerDTO.getPaymentSupported());
        }

        return partnerRepository.save(partner);
    }

    // Deletar uma carteira
    public void deletePartner (String partnerCode)
    {
        Partner partner = getPartnerByCode(partnerCode);
        partnerRepository.delete(partner);
    }
}
