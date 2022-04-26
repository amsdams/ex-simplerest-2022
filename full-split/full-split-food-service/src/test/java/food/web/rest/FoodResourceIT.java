package food.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import food.Crud1Application;
import food.domain.Food;
import food.repository.FoodRepository;
import food.service.dto.FoodDTO;
import food.service.mapper.FoodMapper;
import food.web.rest.FoodResource;
import food.web.util.TestUtil;

/**
 * Integration tests for the {@link FoodResource} REST controller.
 */
@AutoConfigureMockMvc
@SpringBootTest(classes = Crud1Application.class)
class FoodResourceIT {

	private static final String DEFAULT_TITLE = "AAAAAAAAAA";
	private static final String UPDATED_TITLE = "BBBBBBBBBB";

	private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
	private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

	private static final Boolean DEFAULT_PUBLISHED = false;
	private static final Boolean UPDATED_PUBLISHED = true;

	private static final String ENTITY_API_URL = "/api/foods";
	private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

	private static Random random = new Random();
	private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

	@Autowired
	private FoodRepository foodRepository;

	@Autowired
	private FoodMapper foodMapper;

	@Autowired
	private EntityManager em;

	@Autowired
	private MockMvc restFoodMockMvc;

	private Food food;

	/**
	 * Create an entity for this test.
	 *
	 * This is a static method, as tests for other entities might also need it, if
	 * they test an entity which requires the current entity.
	 */
	public static Food createEntity(EntityManager em) {
		Food food = new Food().title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION).published(DEFAULT_PUBLISHED);
		return food;
	}

	/**
	 * Create an updated entity for this test.
	 *
	 * This is a static method, as tests for other entities might also need it, if
	 * they test an entity which requires the current entity.
	 */
	public static Food createUpdatedEntity(EntityManager em) {
		Food food = new Food().title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).published(UPDATED_PUBLISHED);
		return food;
	}

	@BeforeEach
	public void initTest() {
		food = createEntity(em);
	}

	@Test
	@Transactional
	void createFood() throws Exception {
		int databaseSizeBeforeCreate = foodRepository.findAll().size();
		// Create the Food
		FoodDTO foodDTO = foodMapper.toDto(food);
		restFoodMockMvc.perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(foodDTO))).andExpect(status().isCreated());

		// Validate the Food in the database
		List<Food> foodList = foodRepository.findAll();
		assertThat(foodList).hasSize(databaseSizeBeforeCreate + 1);
		Food testFood = foodList.get(foodList.size() - 1);
		assertThat(testFood.getTitle()).isEqualTo(DEFAULT_TITLE);
		assertThat(testFood.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
		assertThat(testFood.getPublished()).isEqualTo(DEFAULT_PUBLISHED);
	}

	@Test
	@Transactional
	void createFoodWithExistingId() throws Exception {
		// Create the Food with an existing ID
		food.setId(1L);
		FoodDTO foodDTO = foodMapper.toDto(food);

		int databaseSizeBeforeCreate = foodRepository.findAll().size();

		// An entity with an existing ID cannot be created, so this API call must fail
		restFoodMockMvc.perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(foodDTO))).andExpect(status().isBadRequest());

		// Validate the Food in the database
		List<Food> foodList = foodRepository.findAll();
		assertThat(foodList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	void checkTitleIsRequired() throws Exception {
		int databaseSizeBeforeTest = foodRepository.findAll().size();
		// set the field null
		food.setTitle(null);

		// Create the Food, which fails.
		FoodDTO foodDTO = foodMapper.toDto(food);

		restFoodMockMvc.perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(foodDTO))).andExpect(status().isBadRequest());

		List<Food> foodList = foodRepository.findAll();
		assertThat(foodList).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	void getAllFoods() throws Exception {
		// Initialize the database
		foodRepository.saveAndFlush(food);

		// Get all the foodList
		restFoodMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.[*].id").value(hasItem(food.getId().intValue())))
				.andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
				.andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
				.andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED.booleanValue())));
	}

	@Test
	@Transactional
	void getFood() throws Exception {
		// Initialize the database
		foodRepository.saveAndFlush(food);

		// Get the food
		restFoodMockMvc.perform(get(ENTITY_API_URL_ID, food.getId())).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.id").value(food.getId().intValue()))
				.andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
				.andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
				.andExpect(jsonPath("$.published").value(DEFAULT_PUBLISHED.booleanValue()));
	}

	@Test
	@Transactional
	void getNonExistingFood() throws Exception {
		// Get the food
		restFoodMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	void putNewFood() throws Exception {
		// Initialize the database
		foodRepository.saveAndFlush(food);

		int databaseSizeBeforeUpdate = foodRepository.findAll().size();

		// Update the food
		Food updatedFood = foodRepository.findById(food.getId()).get();
		// Disconnect from session so that the updates on updatedFood are not directly
		// saved in db
		em.detach(updatedFood);
		updatedFood.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).published(UPDATED_PUBLISHED);
		FoodDTO foodDTO = foodMapper.toDto(updatedFood);

		restFoodMockMvc.perform(put(ENTITY_API_URL_ID, foodDTO.getId()).contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(foodDTO))).andExpect(status().isOk());

		// Validate the Food in the database
		List<Food> foodList = foodRepository.findAll();
		assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
		Food testFood = foodList.get(foodList.size() - 1);
		assertThat(testFood.getTitle()).isEqualTo(UPDATED_TITLE);
		assertThat(testFood.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
		assertThat(testFood.getPublished()).isEqualTo(UPDATED_PUBLISHED);
	}

	@Test
	@Transactional
	void putNonExistingFood() throws Exception {
		int databaseSizeBeforeUpdate = foodRepository.findAll().size();
		food.setId(count.incrementAndGet());

		// Create the Food
		FoodDTO foodDTO = foodMapper.toDto(food);

		// If the entity doesn't have an ID, it will throw BadRequestAlertException
		restFoodMockMvc.perform(put(ENTITY_API_URL_ID, foodDTO.getId()).contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(foodDTO))).andExpect(status().isBadRequest());

		// Validate the Food in the database
		List<Food> foodList = foodRepository.findAll();
		assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
	}

	@Test
	@Transactional
	void putWithIdMismatchFood() throws Exception {
		int databaseSizeBeforeUpdate = foodRepository.findAll().size();
		food.setId(count.incrementAndGet());

		// Create the Food
		FoodDTO foodDTO = foodMapper.toDto(food);

		// If url ID doesn't match entity ID, it will throw BadRequestAlertException
		restFoodMockMvc.perform(put(ENTITY_API_URL_ID, count.incrementAndGet()).contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(foodDTO))).andExpect(status().isBadRequest());

		// Validate the Food in the database
		List<Food> foodList = foodRepository.findAll();
		assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
	}

	@Test
	@Transactional
	void putWithMissingIdPathParamFood() throws Exception {
		int databaseSizeBeforeUpdate = foodRepository.findAll().size();
		food.setId(count.incrementAndGet());

		// Create the Food
		FoodDTO foodDTO = foodMapper.toDto(food);

		// If url ID doesn't match entity ID, it will throw BadRequestAlertException
		restFoodMockMvc.perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(foodDTO))).andExpect(status().isMethodNotAllowed());

		// Validate the Food in the database
		List<Food> foodList = foodRepository.findAll();
		assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
	}

	@Test
	@Transactional
	void partialUpdateFoodWithPatch() throws Exception {
		// Initialize the database
		foodRepository.saveAndFlush(food);

		int databaseSizeBeforeUpdate = foodRepository.findAll().size();

		// Update the food using partial update
		Food partialUpdatedFood = new Food();
		partialUpdatedFood.setId(food.getId());

		partialUpdatedFood.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).published(UPDATED_PUBLISHED);

		restFoodMockMvc
				.perform(
						patch(ENTITY_API_URL_ID, partialUpdatedFood.getId()).contentType("application/merge-patch+json")
								.content(TestUtil.convertObjectToJsonBytes(partialUpdatedFood)))
				.andExpect(status().isOk());

		// Validate the Food in the database
		List<Food> foodList = foodRepository.findAll();
		assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
		Food testFood = foodList.get(foodList.size() - 1);
		assertThat(testFood.getTitle()).isEqualTo(UPDATED_TITLE);
		assertThat(testFood.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
		assertThat(testFood.getPublished()).isEqualTo(UPDATED_PUBLISHED);
	}

	@Test
	@Transactional
	void fullUpdateFoodWithPatch() throws Exception {
		// Initialize the database
		foodRepository.saveAndFlush(food);

		int databaseSizeBeforeUpdate = foodRepository.findAll().size();

		// Update the food using partial update
		Food partialUpdatedFood = new Food();
		partialUpdatedFood.setId(food.getId());

		partialUpdatedFood.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).published(UPDATED_PUBLISHED);

		restFoodMockMvc
				.perform(
						patch(ENTITY_API_URL_ID, partialUpdatedFood.getId()).contentType("application/merge-patch+json")
								.content(TestUtil.convertObjectToJsonBytes(partialUpdatedFood)))
				.andExpect(status().isOk());

		// Validate the Food in the database
		List<Food> foodList = foodRepository.findAll();
		assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
		Food testFood = foodList.get(foodList.size() - 1);
		assertThat(testFood.getTitle()).isEqualTo(UPDATED_TITLE);
		assertThat(testFood.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
		assertThat(testFood.getPublished()).isEqualTo(UPDATED_PUBLISHED);
	}

	@Test
	@Transactional
	void patchNonExistingFood() throws Exception {
		int databaseSizeBeforeUpdate = foodRepository.findAll().size();
		food.setId(count.incrementAndGet());

		// Create the Food
		FoodDTO foodDTO = foodMapper.toDto(food);

		// If the entity doesn't have an ID, it will throw BadRequestAlertException
		restFoodMockMvc.perform(patch(ENTITY_API_URL_ID, foodDTO.getId()).contentType("application/merge-patch+json")
				.content(TestUtil.convertObjectToJsonBytes(foodDTO))).andExpect(status().isBadRequest());

		// Validate the Food in the database
		List<Food> foodList = foodRepository.findAll();
		assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
	}

	@Test
	@Transactional
	void patchWithIdMismatchFood() throws Exception {
		int databaseSizeBeforeUpdate = foodRepository.findAll().size();
		food.setId(count.incrementAndGet());

		// Create the Food
		FoodDTO foodDTO = foodMapper.toDto(food);

		// If url ID doesn't match entity ID, it will throw BadRequestAlertException
		restFoodMockMvc.perform(patch(ENTITY_API_URL_ID, count.incrementAndGet())
				.contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(foodDTO)))
				.andExpect(status().isBadRequest());

		// Validate the Food in the database
		List<Food> foodList = foodRepository.findAll();
		assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
	}

	@Test
	@Transactional
	void patchWithMissingIdPathParamFood() throws Exception {
		int databaseSizeBeforeUpdate = foodRepository.findAll().size();
		food.setId(count.incrementAndGet());

		// Create the Food
		FoodDTO foodDTO = foodMapper.toDto(food);

		// If url ID doesn't match entity ID, it will throw BadRequestAlertException
		restFoodMockMvc.perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json")
				.content(TestUtil.convertObjectToJsonBytes(foodDTO))).andExpect(status().isMethodNotAllowed());

		// Validate the Food in the database
		List<Food> foodList = foodRepository.findAll();
		assertThat(foodList).hasSize(databaseSizeBeforeUpdate);
	}

	@Test
	@Transactional
	void deleteFood() throws Exception {
		// Initialize the database
		foodRepository.saveAndFlush(food);

		int databaseSizeBeforeDelete = foodRepository.findAll().size();

		// Delete the food
		restFoodMockMvc.perform(delete(ENTITY_API_URL_ID, food.getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

		// Validate the database contains one less item
		List<Food> foodList = foodRepository.findAll();
		assertThat(foodList).hasSize(databaseSizeBeforeDelete - 1);
	}
}
