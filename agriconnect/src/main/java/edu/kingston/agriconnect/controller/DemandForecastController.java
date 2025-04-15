package edu.kingston.agriconnect.controller;

import edu.kingston.agriconnect.dto.ForecastRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DemandForecastController {

    @PostMapping("/forecast/demand-for-crop")
    public ResponseEntity<String> getDemandForecastForCrop(@RequestBody ForecastRequest request)
            throws IOException, InterruptedException {
        // Prepare Python command with arguments
        String[] command = {
                "python", "agriconnect/src/main/python/demand-forecast-v2.py",
                request.getCropType(),
                request.getExpectedHarvestDate(),
                String.valueOf(request.getFarmerYield())
        };
        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();
        String result = new String(process.getInputStream().readAllBytes());
        int exitCode = process.waitFor();
        return exitCode == 0 ? ResponseEntity.ok(result) : ResponseEntity.status(500).body("Forecast failed");
    }
}
