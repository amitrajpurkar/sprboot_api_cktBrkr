package com.anr.localmdb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "products")
@JsonInclude(Include.NON_NULL)
public class Product {
    public static final Product EMPTY = new ProductBuilder("00", "empty product").desc("no such product is defined")
            .build();
    @Id
    @NotBlank(message = "Product ID is required")
    @Size(min = 1, max = 50, message = "Product ID must be between 1 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Product ID can only contain alphanumeric characters, hyphens, and underscores")
    @Column(name = "id", nullable = false, length = 50)
    private String id;
    
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters")
    @Column(name = "name", length = 255)
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;
    
    @Pattern(regexp = "^\\$?\\d+(\\.\\d{2})?$", message = "Price must be in format: $10.00 or 10.00")
    @Size(max = 20, message = "Price must not exceed 20 characters")
    @Column(name = "price", length = 20)
    private String price;
    
    @Version  // Enables optimistic locking - prevents lost updates
    private Long version;

    public Product() {
        super();
    }

    public Product(ProductBuilder pb) {
        id = pb.id;
        name = pb.name;
        description = pb.description;
        price = pb.price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public static class ProductBuilder {
        String id;
        String name;
        String description;
        String price;

        public ProductBuilder(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public ProductBuilder desc(String description) {
            this.description = description;
            return this;
        }

        public ProductBuilder price(String price) {
            this.price = price;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }

}
