package com.example.demo.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.dto.ProductDTO;
import com.example.demo.service.mapper.ProductMapper;

@Service
@Transactional
public class ProductService {
	
	private final ProductRepository productRepo;
	private final ProductMapper productMapper;
	
	public ProductService(ProductRepository productRepo, ProductMapper productMapper) {
		
		this.productRepo = productRepo;
		this.productMapper = productMapper;
	}

	
	
    @Transactional(readOnly = true)
	public List<ProductDTO> getProducts() {
		List<Product> actualList = StreamSupport
				  .stream(productRepo.findAll().spliterator(), false)
				  .toList();
		
		return productMapper.toDto(actualList);
	}

	public ProductDTO createProduct(ProductDTO productDTO) {
		Product product = productMapper.toEntity(productDTO);
		return productMapper.toDto(productRepo.save(product));
	}

	public ProductDTO updateProduct(ProductDTO productDTO) {
		Product product = productMapper.toEntity(productDTO);
		return productMapper.toDto(productRepo.save(product));

	}
    @Transactional(readOnly = true)
	public Optional<ProductDTO> getProduct(Long id) {
		return productRepo.findById(id).map(productMapper::toDto);
	}

	public void delete(Long id) {
		productRepo.deleteById(id);		
	}
	
	public boolean exists(Long id) {
		return productRepo.existsById(id);		
	}
}
