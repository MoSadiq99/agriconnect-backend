package edu.kingston.agriconnect.repository;

import edu.kingston.agriconnect.model.BuyerRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuyerRequestRepository extends JpaRepository<BuyerRequest, Long> {
   List<BuyerRequest> findByBuyerId(Long buyerId);

    void deleteAllByBuyerId(Long id);
}

