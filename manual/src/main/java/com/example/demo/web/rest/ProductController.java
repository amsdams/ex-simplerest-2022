package com.example.demo.web.rest;

import java.util.Objects;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.service.dto.ProductDTO;
import com.example.demo.service.impl.ProductService;

@RestController
public class ProductController {
	@Autowired
	ProductService productService;

	@GetMapping(value = "/products")
	public ResponseEntity<Object> getProducts() {
		return new ResponseEntity<>(productService.getProducts(), HttpStatus.OK);
	}

	@PostMapping(value = "/products")
	public ResponseEntity<Object> createProduct(@Valid @RequestBody ProductDTO productDTO) {
		if (productDTO.getId() != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "can not already have an id");

		}
		
		return new ResponseEntity<>(productService.createProduct(productDTO), HttpStatus.CREATED);
	}

	@PutMapping(value = "/products/{id}")
	public ResponseEntity<Object> updateProduct(@PathVariable("id") Long id, @RequestBody ProductDTO productDTO) {

		if (productDTO.getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid id");
		}
		if (!Objects.equals(id, productDTO.getId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid id");
		}

		if (!productService.exists(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "does not exist");
		}

		productDTO.setId(id);
		return new ResponseEntity<>(productService.updateProduct(productDTO), HttpStatus.OK);
	}

	@GetMapping(value = "/products/{id}")
	public ResponseEntity<Object> getProduct(@PathVariable("id") Long id) {
		if (!productService.exists(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "does not exist");
		}
		return new ResponseEntity<>(productService.getProduct(id), HttpStatus.OK);
	}

	@DeleteMapping(value = "/products/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
		if (!productService.exists(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "does not exist");
		}
		productService.delete(id);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
