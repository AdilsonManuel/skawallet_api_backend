/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.controller;

import com.ucan.skawallet.back.end.skawallet.dto.PartnerDTO;
import com.ucan.skawallet.back.end.skawallet.dto.PartnerResponseDTO;
import com.ucan.skawallet.back.end.skawallet.model.Partner;
import com.ucan.skawallet.back.end.skawallet.service.PartnerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author azm
 */
@RestController
@RequestMapping("/api/v1/partners")
@RequiredArgsConstructor
public class PartnerController
{

    private final PartnerService partnerService;

    // Criar um novo parceiro
    @PostMapping("/")
    public ResponseEntity<Partner> createPartner (@RequestBody Partner partner)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerService.createPartner(partner));
    }

    @GetMapping("/")
    public ResponseEntity<List<PartnerResponseDTO>> getAllPartners ()
    {

        // Chama o serviço atualizado, que agora retorna o DTO
        List<PartnerResponseDTO> partners = partnerService.getAllPartners();

        return ResponseEntity.ok(partners); // Retorna 200 OK com a lista de DTOs
    }

    // Buscar parceiro por código
    @GetMapping("/{partnerCode}")
    public ResponseEntity<Partner> getPartnerByCode (@PathVariable String partnerCode)
    {
        return ResponseEntity.ok(partnerService.getPartnerByCode(partnerCode));
    }

    // Deletar uma carteira
    @DeleteMapping("/{partnerCode}")
    public ResponseEntity<String> deletePartner (@PathVariable String partnerCode)
    {
        partnerService.deletePartner(partnerCode);
        return ResponseEntity.ok("Parceiro deletado com sucesso.");
    }

    @PatchMapping("/{partnerCode}")
    public ResponseEntity<Partner> updatePartner (
            @PathVariable String partnerCode,
            @RequestBody PartnerDTO partnerDTO)
    {
        Partner updatedPartnert = partnerService.updatePartner(partnerCode, partnerDTO);
        return ResponseEntity.ok(updatedPartnert);
    }

}
