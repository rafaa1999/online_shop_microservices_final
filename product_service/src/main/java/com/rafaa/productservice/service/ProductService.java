package com.rafaa.productservice.service;

import java.util.List;

import org.springframework.stereotype.Service;


import com.rafaa.productservice.dto.ProductRequest;
import com.rafaa.productservice.dto.ProductResponse;
import com.rafaa.productservice.model.Product;
import com.rafaa.productservice.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

	private final ProductRepository productService;
	
	public void createProduct(ProductRequest productRequest) {
		
		Product product = Product.builder()
								 .name(productRequest.getName())
								 .description(productRequest.getDescription())
								 .price(productRequest.getPrice())
								 .build();
		
		productService.save(product);
		log.info("Product {} is saved", product.getId());
	}
	
	public List<ProductResponse> getAllProducts(){
		List<Product> products = productService.findAll();
		return products.stream().map(product -> mapTpProductResponse(product)).toList();
	}

	private ProductResponse mapTpProductResponse(Product product) {
		return ProductResponse.builder()
							  .id(product.getId())
							  .name(product.getName())
							  .description(product.getDescription())
							  .price(product.getPrice())
							  .build();
	}
	
}
