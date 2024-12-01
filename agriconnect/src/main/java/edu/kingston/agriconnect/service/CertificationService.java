package edu.kingston.agriconnect.service;

import edu.kingston.agriconnect.model.Certification;
import edu.kingston.agriconnect.model.Farmer;
import edu.kingston.agriconnect.model.enums.CertificationStatus;
import edu.kingston.agriconnect.model.enums.CertificationType;
import edu.kingston.agriconnect.repository.CertificationRepository;
import edu.kingston.agriconnect.repository.FarmerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class CertificationService {

    private CertificationRepository certificationRepository;

    private FarmerRepository farmerRepository;

    public Certification certifyFarmer(Long farmerId, String certificationType, String remarks) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new IllegalArgumentException("Farmer not found"));

        Certification certification = new Certification();
        certification.setFarmer(farmer);
        certification.setCertificationType(CertificationType.valueOf(certificationType));
        certification.setCertificationDate(LocalDate.now());
        certification.setExpiryDate(LocalDate.now().plusYears(1)); // 1-year validity
        certification.setStatus(CertificationStatus.ACTIVE);
        certification.setRemarks(remarks);

        return certificationRepository.save(certification);
    }

    public List<Certification> getFarmerCertifications(Long farmerId) {
        return certificationRepository.findByFarmerId(farmerId);
    }

    public void expireCertifications() {
        List<Certification> certifications = certificationRepository.findAll();
        certifications.forEach(cert -> {
            if (cert.getExpiryDate().isBefore(LocalDate.now())) {
                cert.setStatus(CertificationStatus.EXPIRED);
                certificationRepository.save(cert);
            }
        });
    }
}