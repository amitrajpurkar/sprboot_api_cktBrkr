package com.anr.localmdb.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.anr.common.TestHelper;
import com.anr.localmdb.model.Product;
import com.anr.localmdb.model.Product.ProductBuilder;
import com.anr.service.CollectionUpload;
import com.google.gson.Gson;

@SpringBootTest
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository prodRepo;
    @Autowired
    private CollectionUpload upload;

    @Autowired
    private Gson gson;

    @BeforeEach
    void setup() {
        try {
            upload.uploadToCollection("products", "test");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void test_find_productBy_exactName() {
        String givenName = "shaggy";
        List<Product> fetchedProducts = prodRepo.findProductsByName(givenName);

        assertEquals(1, fetchedProducts.size());
    }

    @Test
    void test_find_prodByDescriptionPart() {
        String givenPartOfDescription = "cars1";
        List<Product> fetchedProducts = prodRepo.findProductsWithDescriptionContaining(givenPartOfDescription);

        assertTrue(fetchedProducts.size() > 0);
    }

    @Test
    void insert_one_product() {
        Product giveProduct = getStubbedProduct("ironman", "plastic figuring toy of an avenger superhero", "$5.49");
        Product savedProduct = prodRepo.save(giveProduct);

        assertNotNull(savedProduct, "the saved product was null");
        assertTrue(Objects.equals(savedProduct, giveProduct));
    }

    private Product getStubbedProduct(String name, String desc, String price) {
        String id = RandomStringUtils.randomAlphabetic(10);
        return new ProductBuilder(id, name).desc(desc).price(price).build();
    }

    private Product getSingleProduct(String jsonFile) {
        return gson.fromJson(TestHelper.getJsonXmlStr(jsonFile), Product.class);
    }
}
