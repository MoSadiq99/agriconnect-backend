package edu.kingston.agriconnect.model;

import edu.kingston.agriconnect.model.enums.FarmingMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "farmer_cultivations")
public class FarmerCultivation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", referencedColumnName = "id", nullable = false)
    private User farmer;

    private String cropType;
    private LocalDate cultivationDate;
    private LocalDate harvestDate;
    private String location;
    private String landSize;
    private String expectedYield;
    private FarmingMethod methodOfCultivation;
    private String description;
    private boolean isPublished;
}


