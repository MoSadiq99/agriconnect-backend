package edu.kingston.agriconnect.mapper;

import edu.kingston.agriconnect.dto.BuyerRequestDTO;
import edu.kingston.agriconnect.model.BuyerRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BuyerRequestMapper {

    @Mapping(target = "buyerId", source = "buyer.id")
    BuyerRequestDTO toDto(BuyerRequest entity);

    @Mapping(target = "buyer", ignore = true)
    BuyerRequest toEntity(BuyerRequestDTO dto);

    List<BuyerRequestDTO> toDtoList(List<BuyerRequest> entities);

}
