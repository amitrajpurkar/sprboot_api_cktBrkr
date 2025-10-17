package com.anr.localmdb.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.anr.localmdb.model.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    @Query("{'name': ?0 }")
    List<Product> findProductsByName(String name);

    @Query("{'description': {$in: [ /?0/i ]} }")
    List<Product> findProductsWithDescriptionContaining(String name);

}
