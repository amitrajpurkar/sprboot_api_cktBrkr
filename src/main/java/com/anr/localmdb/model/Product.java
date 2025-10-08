package com.anr.localmdb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Document(collection = "products")
@JsonInclude(Include.NON_NULL)
public class Product {
    public static final Product EMPTY = new ProductBuilder("00", "empty product").desc("no such product is defined")
            .build();
    @Id
    private String id;
    private String name;
    private String description;
    private String price;

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
