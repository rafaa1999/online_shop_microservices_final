package com.rafaa.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rafaa.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {

}
