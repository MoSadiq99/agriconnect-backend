package edu.kingston.agriconnect.model;

import edu.kingston.agriconnect.model.enums.QuantityType;
import edu.kingston.agriconnect.model.enums.RequestType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "buyer_requests")
public class BuyerRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buyer_id", referencedColumnName = "id", nullable = false)
    private User buyer;

    @NotBlank(message = "Crop type is required")
    @Size(max = 100, message = "Crop type must be less than 100 characters")
    @Column(nullable = false, length = 100)
    private String cropType;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Quantity type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuantityType quantityType;

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location must be less than 200 characters")
    @Column(nullable = false, length = 200)
    private String location;

    @NotNull(message = "Start date is required")
//    @FutureOrPresent(message = "Start date must be in the present or future")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    @Column(nullable = false)
    private LocalDate deadline;

    @NotBlank(message = "Status is required")
    @Size(max = 50, message = "Status must be less than 50 characters")
    @Column(nullable = false, length = 50)
    private String status;

    @NotNull(message = "Request type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestType requestType;

    @Size(max = 500, message = "Description must be less than 500 characters")
    @Column(length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

//    @Column(name = "image_url")
//    private String imageUrl;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "in_wishlist")
    private Boolean inWishlist;
}