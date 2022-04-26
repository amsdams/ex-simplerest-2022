package com.example.demo.service.mapper;

import org.mapstruct.Mapper;

import com.example.demo.domain.Product;
import com.example.demo.service.dto.ProductDTO;

/**
 * Mapper for the entity {@link Food} and its DTO {@link FoodDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {}
