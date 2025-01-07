package food.service.mapper;

import food.domain.Food;
import food.service.dto.FoodDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Food} and its DTO {@link FoodDTO}.
 */
@Mapper(componentModel = "spring")
public interface FoodMapper extends EntityMapper<FoodDTO, Food> {
}
