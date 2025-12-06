/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.dto.PartnerDTO;
import com.ucan.skawallet.back.end.skawallet.dto.PartnerResponseDTO;
import com.ucan.skawallet.back.end.skawallet.dto.ProdutoDTO;
import com.ucan.skawallet.back.end.skawallet.exception.ResourceNotFoundException;
import com.ucan.skawallet.back.end.skawallet.model.Partner;
import com.ucan.skawallet.back.end.skawallet.repository.PartnerRepository;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author azm
 */
@Service
@RequiredArgsConstructor
public class PartnerService {

    private static final Pattern PARTNER_CODE_PATTERN = Pattern.compile("[^A-Z0-9]+");
    private final PartnerRepository partnerRepository;

    // Criar um novo parceiro
    @Transactional
    public Partner createPartner(Partner partner) {
        partner.setPartnerCode(generatePartnerCode(partner.getName()));
        return partnerRepository.save(partner);
    }

    // Buscar todos os parceiros e retornar como DTO
    @Transactional(readOnly = true)
    public List<PartnerResponseDTO> getAllPartners() {
        return partnerRepository.findAll().stream()
                .map(this::mapToPartnerResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar parceiro pelo código único
    @Transactional(readOnly = true)
    public Partner getPartnerByCode(String partnerCode) {
        return partnerRepository.findByPartnerCode(partnerCode)
                .orElseThrow(() -> new ResourceNotFoundException("Parceiro não encontrado com código: " + partnerCode));
    }

    // Gerar código único baseado no nome
    private String generatePartnerCode(String name) {
        // Garante que o código tem no máximo 10 caracteres e é sanitizado
        String sanitizedName = PARTNER_CODE_PATTERN.matcher(name.toUpperCase()).replaceAll("_");
        return sanitizedName.substring(0, Math.min(sanitizedName.length(), 10));
    }

    // Atualizar parcialmente um parceiro
    @Transactional
    public Partner updatePartner(String partnerCode, PartnerDTO partnerDTO) {
        Partner partner = getPartnerByCode(partnerCode);

        if (partnerDTO.getName() != null) {
            partner.setName(partnerDTO.getName());
        }
        if (partnerDTO.getContactInfo() != null) {
            partner.setContactInfo(partnerDTO.getContactInfo());
        }
        if (partnerDTO.getDescription() != null) {
            partner.setDescription(partnerDTO.getDescription());
        }
        if (partnerDTO.getCategory() != null) {
            partner.setCategory(partnerDTO.getCategory());
        }
        if (partnerDTO.getPaymentSupported() != null) {
            partner.setPaymentSupported(partnerDTO.getPaymentSupported());
        }

        return partnerRepository.save(partner);
    }

    // Deletar um parceiro
    @Transactional
    public void deletePartner(String partnerCode) {
        Partner partner = getPartnerByCode(partnerCode);
        partnerRepository.delete(partner);
    }

    // --- MÉTODO DE MAPEAMENTO DA ENTIDADE PARA DTO ---
    private PartnerResponseDTO mapToPartnerResponseDTO(Partner partner) {
        List<ProdutoDTO> produtosDto = partner.getProdutos().stream()
                .map(produto -> {
                    ProdutoDTO dto = new ProdutoDTO();
                    dto.setId(produto.getId());
                    dto.setNome(produto.getNome());
                    dto.setPreco(produto.getPreco());
                    return dto;
                })
                .collect(Collectors.toList());

        return PartnerResponseDTO.builder()
                .pkPartners(partner.getPkPartners())
                .partnerCode(partner.getPartnerCode())
                .name(partner.getName())
                .description(partner.getDescription())
                .category(partner.getCategory().name())
                .paymentSupported(partner.getPaymentSupported())
                .contactInfo(partner.getContactInfo())
                .createdAt(partner.getCreatedAt())
                .produtos(produtosDto)
                .build();
    }
}
