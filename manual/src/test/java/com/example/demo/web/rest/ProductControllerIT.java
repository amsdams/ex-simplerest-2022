package com.example.demo.web.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.demo.Crud1Application;
import com.example.demo.domain.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.dto.ProductDTO;
import com.example.demo.service.mapper.ProductMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@AutoConfigureMockMvc
@SpringBootTest(classes = Crud1Application.class)
public class ProductControllerIT {

	private static final String UPDATED_EMAIL = "updated email";

	private static final String UPDATED_NAME = "updated name";

	private static final String DEFAULT_NAME = "name";

	private static final String DEFAULT_EMAIL = "email";

	@Autowired
	ProductRepository productRepo;

	@Autowired
	ProductMapper productMapper;

	@Autowired
	private MockMvc restFoodMockMvc;

	private Product product;

	@BeforeEach
	public void initTest() {
		product = new Product();
		product.setEmail(DEFAULT_EMAIL);
		product.setName(DEFAULT_NAME);
	}

	@Test
	void getProducts() throws Exception {
		productRepo.save(product);
		restFoodMockMvc.perform(get("/products")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
				.andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
				.andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));
	}

	@Test
	void createProduct() throws Exception {
		ProductDTO productDTO = productMapper.toDto(product);

		restFoodMockMvc
				.perform(post("/products").contentType(MediaType.APPLICATION_JSON)
						.content(convertObjectToJsonBytes(productDTO)))
				.andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				// .andExpect(jsonPath("$.id").value(hasItem(product.getId().intValue())))
				.andExpect(jsonPath("$.name").value(DEFAULT_NAME)).andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));
	}

	@Test
	void createProductWithID() throws Exception {
		product.setId(Long.MAX_VALUE);
		ProductDTO productDTO = productMapper.toDto(product);

		restFoodMockMvc
				.perform(post("/products").contentType(MediaType.APPLICATION_JSON)
						.content(convertObjectToJsonBytes(productDTO)))
				.andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	void createProductNoName() throws Exception {
		product.setName(null);

		ProductDTO productDTO = productMapper.toDto(product);

		restFoodMockMvc
				.perform(post("/products").contentType(MediaType.APPLICATION_JSON)
						.content(convertObjectToJsonBytes(productDTO)))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.name").value("must not be null"));

	}

	@Test
	void updateProduct() throws IOException, Exception {
		productRepo.save(product);

		product.setName(UPDATED_NAME);
		product.setEmail(UPDATED_EMAIL);
		ProductDTO productDTO = productMapper.toDto(product);

		restFoodMockMvc
				.perform(put("/products/{id}", product.getId()).contentType(MediaType.APPLICATION_JSON)
						.content(convertObjectToJsonBytes(productDTO)))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.id").value(product.getId().intValue()))
				.andExpect(jsonPath("$.name").value(UPDATED_NAME)).andExpect(jsonPath("$.email").value(UPDATED_EMAIL));
	}

	@Test
	void updateProductWithoutId() throws IOException, Exception {
		productRepo.save(product);

		ProductDTO productDTO = productMapper.toDto(product);
		productDTO.setId(null);
		restFoodMockMvc
				.perform(put("/products/{id}", product.getId()).contentType(MediaType.APPLICATION_JSON)
						.content(convertObjectToJsonBytes(productDTO)))
				.andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	void updateProductWithoutId2() throws IOException, Exception {
		productRepo.save(product);

		ProductDTO productDTO = productMapper.toDto(product);

		restFoodMockMvc
				.perform(put("/products/{id}", Long.MAX_VALUE).contentType(MediaType.APPLICATION_JSON)
						.content(convertObjectToJsonBytes(productDTO)))
				.andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	void getProduct() throws Exception {
		productRepo.save(product);
		restFoodMockMvc.perform(get("/products/{id}", product.getId())).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.id").value(product.getId().intValue()))
				.andExpect(jsonPath("$.name").value(DEFAULT_NAME)).andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));
	}

	@Test
	void getProductDoesNotExist() throws Exception {
		productRepo.save(product);
		restFoodMockMvc.perform(get("/products/{id}", -1)).andExpect(status().isNotFound()).andDo(print());
	}

	@Test
	void delete() throws Exception {
		productRepo.save(product);
		restFoodMockMvc.perform(
				MockMvcRequestBuilders.delete("/products/{id}", product.getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}

	@Test
	void deleteDoesNotExist() throws Exception {
		productRepo.save(product);
		restFoodMockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", -1).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andDo(print());
	}

	private static final ObjectMapper mapper = createObjectMapper();

	private static ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		mapper.registerModule(new JavaTimeModule());
		return mapper;
	}

	/**
	 * Convert an object to JSON byte array.
	 *
	 * @param object the object to convert.
	 * @return the JSON byte array.
	 * @throws IOException
	 */
	public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
		return mapper.writeValueAsBytes(object);
	}
}
