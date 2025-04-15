package edu.kingston.agriconnect.dto;


import lombok.Data;

@Data
public class ForecastRequest {
    private String cropType;
    private String expectedHarvestDate;  // ISO format, e.g., "2025-07-15"
    private double farmerYield = 0;      // Optional, default 0
}