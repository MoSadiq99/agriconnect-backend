package edu.kingston.agriconnect.model;

import edu.kingston.agriconnect.model.enums.RequestType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "buyer_requests")
public class BuyerRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "buyer_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private User buyer;

    private String cropType;
    private Integer quantity;
    private String location;
    private LocalDate startDate;
    private LocalDate deadline;
    private String status;

    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    @Column(length = 500)
    private String description;
}
