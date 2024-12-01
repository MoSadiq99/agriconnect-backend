package edu.kingston.agriconnect.repository;

import edu.kingston.agriconnect.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<Certification> findByFarmerId(Long farmerId);
}
