package food.service;

import java.util.List;
import java.util.Optional;

import food.service.dto.FoodDTO;

/**
 * Service Interface for managing {@link com.amsdams.ex.domain.Food}.
 */
public interface FoodService {
	/**
	 * Save a food.
	 *
	 * @param foodDTO the entity to save.
	 * @return the persisted entity.
	 */
	FoodDTO save(FoodDTO foodDTO);

	/**
	 * Partially updates a food.
	 *
	 * @param foodDTO the entity to update partially.
	 * @return the persisted entity.
	 */
	Optional<FoodDTO> partialUpdate(FoodDTO foodDTO);

	/**
	 * Get all the foods.
	 *
	 * @return the list of entities.
	 */
	List<FoodDTO> findAll();

	/**
	 * Get the "id" food.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<FoodDTO> findOne(Long id);

	/**
	 * Delete the "id" food.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);
}
