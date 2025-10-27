/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.dto.PartnerDTO;
import com.ucan.skawallet.back.end.skawallet.dto.PartnerResponseDTO;
import com.ucan.skawallet.back.end.skawallet.dto.ProdutoDTO;
import com.ucan.skawallet.back.end.skawallet.model.Partner;
import com.ucan.skawallet.back.end.skawallet.repository.PartnerRepository;
import java.util.List;
import java.util.stream.Collectors;
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

    // Buscar todos os parceiros e retornar como DTO (CORRIGIDO)
    // Retorna List<PartnerResponseDTO> para evitar a recursão na serialização
    public List<PartnerResponseDTO> getAllPartners ()
    {
        return partnerRepository.findAll().stream()
                .map(this::mapToPartnerResponseDTO) // Transforma a entidade no DTO
                .collect(Collectors.toList());
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
        // Garante que o código tem no máximo 10 caracteres e é sanitizado
        String sanitizedName = name.toUpperCase().replaceAll("[^A-Z0-9]+", "_");
        return sanitizedName.substring(0, Math.min(sanitizedName.length(), 10));
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
        // Assumindo que o PartnerDTO tem getCategory() que retorna PartnerCategory
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

    // Deletar um parceiro
    public void deletePartner (String partnerCode)
    {
        Partner partner = getPartnerByCode(partnerCode);
        partnerRepository.delete(partner);
    }

    // --- MÉTODO DE MAPEAMENTO DA ENTIDADE PARA DTO (CORRIGIDO) ---
    private PartnerResponseDTO mapToPartnerResponseDTO (Partner partner)
    {

        // Mapeia a lista de Produtos para ProdutoDTO (quebrando a recursão)
        List<ProdutoDTO> produtosDto = partner.getProdutos().stream()
                .map(produto ->
                {
                    ProdutoDTO dto = new ProdutoDTO();
                    dto.setId(produto.getId());
                    dto.setNome(produto.getNome());
                    dto.setPreco(produto.getPreco());
                    // dto.setDescricao(...) - REMOVIDO: O método getDescricao() não existe na entidade Produto.
                    return dto;
                })
                .collect(Collectors.toList());

        return PartnerResponseDTO.builder()
                .pkPartners(partner.getPkPartners())
                .partnerCode(partner.getPartnerCode())
                .name(partner.getName())
                .description(partner.getDescription())
                // CORRIGIDO: Conversão de Enum para String (.name())
                .category(partner.getCategory().name())
                .paymentSupported(partner.getPaymentSupported())
                .contactInfo(partner.getContactInfo())
                .createdAt(partner.getCreatedAt())
                .produtos(produtosDto)
                .build();
    }
}
