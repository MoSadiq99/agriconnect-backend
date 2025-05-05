package edu.kingston.agriconnect.mapper;

import edu.kingston.agriconnect.dto.FarmerListingDTO;
import edu.kingston.agriconnect.model.FarmerListing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FarmerListingMapper {

    @Mapping(target = "farmerId", source = "farmer.id")
    FarmerListingDTO toDto(FarmerListing entity);

    @Mapping(target = "farmer", ignore = true) //! Farmer will be set separately
    FarmerListing toEntity(FarmerListingDTO dto);

    List<FarmerListingDTO> toDtoList(List<FarmerListing> entities);
}
