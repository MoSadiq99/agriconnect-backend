package edu.kingston.agriconnect.model;

import edu.kingston.agriconnect.model.enums.CertificationStatus;
import edu.kingston.agriconnect.model.enums.CertificationType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "certifications")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "farmer_id", referencedColumnName = "id", nullable = false)
    private Farmer farmer;

    private CertificationType certificationType; // e.g., Organic, Sustainable, etc.
    private LocalDate certificationDate;
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private CertificationStatus status; // ACTIVE, EXPIRED, PENDING

    private String remarks; // Notes about certification process or inspections
}
