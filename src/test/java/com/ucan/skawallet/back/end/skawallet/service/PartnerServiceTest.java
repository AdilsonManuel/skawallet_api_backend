package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.dto.PartnerDTO;
import com.ucan.skawallet.back.end.skawallet.enums.PartnerCategory;
import com.ucan.skawallet.back.end.skawallet.exception.ResourceNotFoundException;
import com.ucan.skawallet.back.end.skawallet.model.Partner;
import com.ucan.skawallet.back.end.skawallet.repository.PartnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartnerServiceTest {

    @Mock
    private PartnerRepository partnerRepository;

    @InjectMocks
    private PartnerService partnerService;

    private Partner partner;

    @BeforeEach
    void setUp() {
        partner = new Partner();
        partner.setPkPartners(1L);
        partner.setName("Test Partner");
        partner.setPartnerCode("TEST_PARTN");
        partner.setCategory(PartnerCategory.RETAIL); // Assuming RETAIL is a valid enum value
    }

    @Test
    void createPartner_ShouldGenerateCodeAndSave() {
        when(partnerRepository.save(any(Partner.class))).thenReturn(partner);

        Partner createdPartner = partnerService.createPartner(partner);

        assertNotNull(createdPartner);
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }

    @Test
    void getPartnerByCode_ShouldReturnPartner_WhenFound() {
        when(partnerRepository.findByPartnerCode("TEST_PARTN")).thenReturn(Optional.of(partner));

        Partner foundPartner = partnerService.getPartnerByCode("TEST_PARTN");

        assertNotNull(foundPartner);
        assertEquals("TEST_PARTN", foundPartner.getPartnerCode());
    }

    @Test
    void getPartnerByCode_ShouldThrowException_WhenNotFound() {
        when(partnerRepository.findByPartnerCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> partnerService.getPartnerByCode("INVALID"));
    }

    @Test
    void updatePartner_ShouldUpdateFields_WhenFound() {
        when(partnerRepository.findByPartnerCode("TEST_PARTN")).thenReturn(Optional.of(partner));
        when(partnerRepository.save(any(Partner.class))).thenReturn(partner);

        PartnerDTO partnerDTO = new PartnerDTO();
        partnerDTO.setName("Updated Name");

        Partner updatedPartner = partnerService.updatePartner("TEST_PARTN", partnerDTO);

        assertEquals("Updated Name", updatedPartner.getName());
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }

    @Test
    void deletePartner_ShouldDelete_WhenFound() {
        when(partnerRepository.findByPartnerCode("TEST_PARTN")).thenReturn(Optional.of(partner));

        partnerService.deletePartner("TEST_PARTN");

        verify(partnerRepository, times(1)).delete(partner);
    }
}
