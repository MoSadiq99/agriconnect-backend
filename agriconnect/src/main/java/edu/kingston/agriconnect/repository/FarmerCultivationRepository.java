package edu.kingston.agriconnect.repository;

import edu.kingston.agriconnect.model.FarmerCultivation;
import edu.kingston.agriconnect.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmerCultivationRepository extends JpaRepository<FarmerCultivation, Long> {

    @Query("SELECT fc FROM FarmerCultivation fc JOIN FETCH fc.farmer WHERE fc.farmer.id = :farmerId")
    List<FarmerCultivation> findByFarmerId(@Param("farmerId") Long farmerId);

    List<FarmerCultivation> findByFarmer(User farmer);

    void deleteAllByFarmerId(Long farmerId);

    // Pagination support
    Page<FarmerCultivation> findAll(Pageable pageable);
}