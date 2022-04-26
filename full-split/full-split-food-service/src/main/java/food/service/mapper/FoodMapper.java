
package food.service.mapper;

import org.mapstruct.Mapper;

import food.domain.Food;
import food.service.dto.FoodDTO;

/**
 * Mapper for the entity {@link Food} and its DTO {@link FoodDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FoodMapper extends EntityMapper<FoodDTO, Food> {
}
