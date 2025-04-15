package edu.kingston.agriconnect.model;

import edu.kingston.agriconnect.model.enums.FarmingMethod;
import edu.kingston.agriconnect.model.enums.ListingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "farmer_listings")
public class FarmerListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "farmer_id", referencedColumnName = "id", nullable = false)
    private User farmer;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "cultivation_id", referencedColumnName = "id", nullable = false)
    private FarmerCultivation cultivation;

//    @ManyToOne
//    @JoinColumn(name = "cultivation_id", nullable = false, foreignKey = @ForeignKey(name = "fk_farmer_cultivation", foreignKeyDefinition = "FOREIGN KEY (cultivation_id) REFERENCES farmer_cultivations(id) ON DELETE CASCADE"))
//    private FarmerCultivation cultivation;

//    Solution 2: Use ON DELETE CASCADE at the Database Level
//    If you prefer database-level control, define your foreign key with ON DELETE CASCADE. Modify your FarmerListing entity like this:

    private String cropType;
    private String quantity;
    private String unit;
    private String location;
    private LocalDate availableDateFrom;
    private double price;
    private FarmingMethod methodOfCultivation;
    @Enumerated(EnumType.STRING)
    private ListingStatus status;

    @Column(length = 500)
    private String description;
}
