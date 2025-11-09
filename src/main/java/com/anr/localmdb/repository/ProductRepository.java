package com.anr.localmdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.anr.localmdb.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    // JPA method name query - finds products by exact name match
    List<Product> findProductsByName(String name);

    // JPQL query - finds products with description containing the text (case-insensitive)
    @Query("SELECT p FROM Product p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<Product> findProductsWithDescriptionContaining(String text);

}
