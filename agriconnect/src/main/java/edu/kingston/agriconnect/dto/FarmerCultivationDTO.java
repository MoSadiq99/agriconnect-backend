package edu.kingston.agriconnect.dto;

import edu.kingston.agriconnect.model.enums.FarmingMethod;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class FarmerCultivationDTO implements Serializable {
    private Long id;
    private Long farmerId;
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
