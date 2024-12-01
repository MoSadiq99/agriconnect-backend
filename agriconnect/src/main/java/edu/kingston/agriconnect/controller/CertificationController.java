package edu.kingston.agriconnect.controller;

import edu.kingston.agriconnect.model.Certification;
import edu.kingston.agriconnect.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/certifications")
@RequiredArgsConstructor
public class CertificationController {

    private CertificationService certificationService;

    @PostMapping("/certify/{farmerId}")
    public Certification certifyFarmer(@PathVariable Long farmerId,
                                       @RequestParam String certificationType,
                                       @RequestParam String remarks) {
        return certificationService.certifyFarmer(farmerId, certificationType, remarks);
    }

    @GetMapping("/{farmerId}")
    public List<Certification> getFarmerCertifications(@PathVariable Long farmerId) {
        return certificationService.getFarmerCertifications(farmerId);
    }

    @PostMapping("/expire")
    public void expireCertifications() {
        certificationService.expireCertifications();
    }
}