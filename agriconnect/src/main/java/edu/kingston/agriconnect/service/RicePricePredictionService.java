package edu.kingston.agriconnect.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RicePricePredictionService {

    private final String fastApiUrl = "http://localhost:5000/pred" +
            "ict"; // FastAPI endpoint URL

    public Map<String, Double> predictPrice(int month, double exchangeRate, double fuelPrice) {
        // Calculate lagged features and rolling averages (example logic)
        double exchangeRateLag1 = exchangeRate * 1.1; // Example: 10% increase
        double fuelPriceLag1 = fuelPrice * 1.05; // Example: 5% increase
        double exchangeRateRollingAvg = (exchangeRate + exchangeRateLag1) / 2;
        double fuelPriceRollingAvg = (fuelPrice + fuelPriceLag1) / 2;

        // Prepare the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("exchange_rate", exchangeRate);
        requestBody.put("fuel_price", fuelPrice);
        requestBody.put("month", month);
        requestBody.put("exchange_rate_lag1", exchangeRateLag1);
        requestBody.put("fuel_price_lag1", fuelPriceLag1);
        requestBody.put("exchange_rate_rolling_avg", exchangeRateRollingAvg);
        requestBody.put("fuel_price_rolling_avg", fuelPriceRollingAvg);

        // Call the FastAPI endpoint
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Double> response = restTemplate.postForObject(fastApiUrl, requestBody, Map.class);

        return response;
    }
}