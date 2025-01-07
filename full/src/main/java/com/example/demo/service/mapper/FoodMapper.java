package com.example.demo.service.mapper;

import com.example.demo.domain.Food;
import com.example.demo.service.dto.FoodDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Food} and its DTO {@link FoodDTO}.
 */
@Mapper(componentModel = "spring")
public interface FoodMapper extends EntityMapper<FoodDTO, Food> {
}
