package edu.kingston.agriconnect.dto;

import edu.kingston.agriconnect.model.enums.QuantityType;
import edu.kingston.agriconnect.model.enums.RequestType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyerRequestDTO {

    private Long id;

    private Long buyerId;

    @NotBlank(message = "Crop type is required")
    @Size(max = 100, message = "Crop type must be less than 100 characters")
    private String cropType;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Quantity type is required")
    private QuantityType quantityType;

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location must be less than 200 characters")
    private String location;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDate startDate;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    private LocalDate deadline;

    @NotBlank(message = "Status is required")
    @Size(max = 50, message = "Status must be less than 50 characters")
    private String status;

    @NotNull(message = "Request type is required")
    private RequestType requestType;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

//    private String imageUrl;

    private Integer rating;

    private Boolean inWishlist;
}