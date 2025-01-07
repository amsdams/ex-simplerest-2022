package com.example.demo.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDTO {
	private Long id;
	@NotNull
	private String name;
	@NotNull
	private String email;
}