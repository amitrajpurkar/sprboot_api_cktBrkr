package com.anr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anr.localmdb.model.Product;
import com.anr.localmdb.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;

    public Product saveOne(Product prod) {
        return productRepo.save(prod);
    }

    public List<Product> saveBatch(List<Product> products) {
        return productRepo.saveAll(products);
    }

    public Product findById(String id) {
        Optional<Product> product = productRepo.findById(id);
        if (product.isEmpty()) {
            return Product.EMPTY;
        } else {
            return product.get();
        }
    }

    public List<Product> findByExactName(String name) {
        return productRepo.findProductsByName(name);
    }

    public List<Product> findByDescContaining(String textpart) {
        return productRepo.findProductsWithDescriptionContaining(textpart);
    }

}
