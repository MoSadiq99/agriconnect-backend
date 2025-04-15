package edu.kingston.agriconnect.controller;


import edu.kingston.agriconnect.service.RicePricePredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PricePredictionController {

    private final RicePricePredictionService pricePredictionService;

    @PostMapping("/predict")
    public Map<String, Double> predictRicePrice(@RequestBody Map<String, Object> requestBody) {
        try {
            int year = parseInteger(requestBody.get("year"), "Year");
            int month = parseInteger(requestBody.get("month"), "Month");
            double exchangeRate = parseDouble(requestBody.get("exchangeRate"), "Exchange Rate");
            double fuelPrice = parseDouble(requestBody.get("fuelPrice"), "Fuel Price");

            return pricePredictionService.predictPrice(month, exchangeRate, fuelPrice);
        } catch (IllegalArgumentException e) {
            return Map.of("error", -1.0); // Return an error response
        }
    }

    private int parseInteger(Object value, String fieldName) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(fieldName + " must be a valid integer.");
            }
        }
        throw new IllegalArgumentException(fieldName + " is required and must be a number.");
    }

    private double parseDouble(Object value, String fieldName) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(fieldName + " must be a valid number.");
            }
        }
        throw new IllegalArgumentException(fieldName + " is required and must be a number.");
    }
}
