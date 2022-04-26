package food.service.mapper;

import org.junit.jupiter.api.BeforeEach;

import food.service.mapper.FoodMapper;

class FoodMapperTest {

	private FoodMapper foodMapper;

	@BeforeEach
	public void setUp() {
		foodMapper = new FoodMapperImpl();
	}
}
