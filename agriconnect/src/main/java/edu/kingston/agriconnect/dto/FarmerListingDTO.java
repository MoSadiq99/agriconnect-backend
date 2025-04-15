package edu.kingston.agriconnect.dto;

import edu.kingston.agriconnect.model.enums.FarmingMethod;
import edu.kingston.agriconnect.model.enums.ListingStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class FarmerListingDTO implements Serializable {

    private Long id;
    private Long farmerId;
    private Long cultivationId;
    private String cropType;
    private String quantity;
    private Double price;
    private String unit;
    private String description;
    private LocalDate availableDateFrom;
    private String location;
    private ListingStatus status;
    private FarmingMethod methodOfCultivation;
}
