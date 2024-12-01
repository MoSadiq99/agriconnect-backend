package edu.kingston.agriconnect.model;

import edu.kingston.agriconnect.model.enums.ListingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "farmer_listings")
public class FarmerListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "farmer_id", referencedColumnName = "id", nullable = false)
    private User farmer;

    @ManyToOne
    @JoinColumn(name = "cultivation_id", referencedColumnName = "id", nullable = false)
    private FarmerCultivation cultivation;

    private String cropType;
    private Integer quantity;
    private String location;
    private LocalDate availableDateFrom;

    @Enumerated(EnumType.STRING)
    private ListingStatus status;

    @Column(length = 500)
    private String description;
}
