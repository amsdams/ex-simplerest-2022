package food.web.rest;

import food.repository.FoodRepository;
import food.service.FoodService;
import food.service.dto.FoodDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.rest.errors.BadRequestAlertException;
import web.util.HeaderUtil;
import web.util.ResponseUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.amsdams.example.domain.Food}.
 */
@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class FoodResource {

	private static final String ENTITY_NAME = "food";
	private final FoodService foodService;
	private final FoodRepository foodRepository;
	@Value("${amsdams.clientApp.name}:bla")
	private String applicationName;

	/**
	 * {@code POST  /foods} : Create a new food.
	 *
	 * @param foodDTO the foodDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 * body the new foodDTO, or with status {@code 400 (Bad Request)} if the
	 * food has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/foods")
	public ResponseEntity<FoodDTO> createFood(@Valid @RequestBody FoodDTO foodDTO) throws URISyntaxException {
		log.debug("REST request to save Food : {}", foodDTO);
		if (foodDTO.getId() != null) {
			throw new BadRequestAlertException("A new food cannot already have an ID", ENTITY_NAME, "idexists");
		}
		FoodDTO result = foodService.save(foodDTO);
		return ResponseEntity
				.created(new URI("/api/foods/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /foods/:id} : Updates an existing food.
	 *
	 * @param id      the id of the foodDTO to save.
	 * @param foodDTO the foodDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 * the updated foodDTO, or with status {@code 400 (Bad Request)} if the
	 * foodDTO is not valid, or with status
	 * {@code 500 (Internal Server Error)} if the foodDTO couldn't be
	 * updated.
	 */
	@PutMapping("/foods/{id}")
	public ResponseEntity<FoodDTO> updateFood(@PathVariable(value = "id", required = false) final Long id,
											  @Valid @RequestBody FoodDTO foodDTO) {
		log.debug("REST request to update Food : {}, {}", id, foodDTO);
		if (foodDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		if (!Objects.equals(id, foodDTO.getId())) {
			throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
		}

		if (!foodRepository.existsById(id)) {
			throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
		}

		FoodDTO result = foodService.save(foodDTO);
		return ResponseEntity.ok().headers(
						HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, foodDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PATCH  /foods/:id} : Partial updates given fields of an existing food,
	 * field will ignore if it is null
	 *
	 * @param id      the id of the foodDTO to save.
	 * @param foodDTO the foodDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 * the updated foodDTO, or with status {@code 400 (Bad Request)} if the
	 * foodDTO is not valid, or with status {@code 404 (Not Found)} if the
	 * foodDTO is not found, or with status
	 * {@code 500 (Internal Server Error)} if the foodDTO couldn't be
	 * updated.
	 */
	@PatchMapping(value = "/foods/{id}", consumes = {"application/json", "application/merge-patch+json"})
	public ResponseEntity<FoodDTO> partialUpdateFood(@PathVariable(value = "id", required = false) final Long id,
													 @NotNull @RequestBody FoodDTO foodDTO) {
		log.debug("REST request to partial update Food partially : {}, {}", id, foodDTO);
		if (foodDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		if (!Objects.equals(id, foodDTO.getId())) {
			throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
		}

		if (!foodRepository.existsById(id)) {
			throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
		}

		Optional<FoodDTO> result = foodService.partialUpdate(foodDTO);

		return ResponseUtil.wrapOrNotFound(result,
				HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, foodDTO.getId().toString()));
	}

	/**
	 * {@code GET  /foods} : get all the foods.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 * of foods in body.
	 */
	@GetMapping("/foods")
	public List<FoodDTO> getAllFoods() {
		log.debug("REST request to get all Foods");
		return foodService.findAll();
	}

	/**
	 * {@code GET  /foods/:id} : get the "id" food.
	 *
	 * @param id the id of the foodDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 * the foodDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/foods/{id}")
	public ResponseEntity<FoodDTO> getFood(@PathVariable Long id) {
		log.debug("REST request to get Food : {}", id);
		Optional<FoodDTO> foodDTO = foodService.findOne(id);
		return ResponseUtil.wrapOrNotFound(foodDTO);
	}

	/**
	 * {@code DELETE  /foods/:id} : delete the "id" food.
	 *
	 * @param id the id of the foodDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@DeleteMapping("/foods/{id}")
	public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
		log.debug("REST request to delete Food : {}", id);
		foodService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
				.build();
	}
}
