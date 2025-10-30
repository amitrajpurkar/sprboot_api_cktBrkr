package com.anr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anr.localmdb.model.Product;
import com.anr.localmdb.repository.ProductRepository;

@Service
@Transactional(readOnly = true)  // Default for all methods (performance optimization)
public class ProductService {

    private final ProductRepository productRepo;

    // Constructor injection (better than field injection)
    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    @Transactional  // Override for write operations
    public Product saveOne(Product prod) {
        return productRepo.save(prod);
    }

    @Transactional  // Ensures all saves happen in one transaction
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

    public List<Product> findAll() {
        return productRepo.findAll();
    }

    @Transactional  // Atomic update operation - prevents race conditions
    public Product updateProduct(String id, Product product) {
        return productRepo.findById(id)
            .map(existing -> {
                product.setId(id); // Ensure the ID matches
                // Preserve version for optimistic locking if it exists
                if (existing.getVersion() != null) {
                    product.setVersion(existing.getVersion());
                }
                return productRepo.save(product);
            })
            .orElse(Product.EMPTY);
    }

    @Transactional  // Atomic delete operation
    public boolean deleteProduct(String id) {
        if (productRepo.existsById(id)) {
            productRepo.deleteById(id);
            return true;
        }
        return false;
    }

}
