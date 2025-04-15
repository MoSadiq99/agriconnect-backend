package edu.kingston.agriconnect.mapper;

import edu.kingston.agriconnect.dto.FarmerCultivationDTO;
import edu.kingston.agriconnect.model.FarmerCultivation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FarmerCultivationMapper {
    FarmerCultivationMapper INSTANCE = Mappers.getMapper(FarmerCultivationMapper.class);

    @Mapping(target = "farmerId", source = "farmer.id")
    FarmerCultivationDTO toDto(FarmerCultivation entity);

    @Mapping(target = "farmer", ignore = true) // Farmer will be set separately
    FarmerCultivation toEntity(FarmerCultivationDTO dto);

    List<FarmerCultivationDTO> toDtoList(List<FarmerCultivation> entities);
}