package com.anr.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.anr.localmdb.model.Product;
import com.anr.localmdb.model.Product.ProductBuilder;
import com.anr.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for ProductController
 * Tests all CRUD endpoints: GET, POST, PUT, DELETE
 * 
 * @author amitr
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private static final String BASE_URI = "/api/v1/products";

    // Test GET /api/v1/products - Get all products (success with multiple products)
    @Test
    void test_getAllProducts_success() throws Exception {
        List<Product> products = Arrays.asList(
                createProduct("P001", "Laptop", "High-performance laptop", "1299.99"),
                createProduct("P002", "Mouse", "Wireless mouse", "29.99"),
                createProduct("P003", "Keyboard", "Mechanical keyboard", "89.99")
        );

        when(productService.findAll()).thenReturn(products);

        mockMvc.perform(get(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value("P001"))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].id").value("P002"))
                .andExpect(jsonPath("$[2].id").value("P003"));
    }

    // Test GET /api/v1/products - Get all products (empty list)
    @Test
    void test_getAllProducts_emptyList() throws Exception {
        when(productService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Test GET /api/v1/products/{id} - Get product by ID (success)
    @Test
    void test_getProductById_success() throws Exception {
        Product product = createProduct("P001", "Laptop", "High-performance laptop", "1299.99");
        
        when(productService.findById("P001")).thenReturn(product);

        mockMvc.perform(get(BASE_URI + "/P001")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("P001"))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("High-performance laptop"))
                .andExpect(jsonPath("$.price").value("1299.99"));
    }

    // Test GET /api/v1/products/{id} - Product not found
    @Test
    void test_getProductById_notFound() throws Exception {
        when(productService.findById("P999")).thenReturn(Product.EMPTY);

        mockMvc.perform(get(BASE_URI + "/P999")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Test POST /api/v1/products - Create product (success)
    @Test
    void test_createProduct_success() throws Exception {
        Product newProduct = createProduct("P004", "Monitor", "4K Monitor", "499.99");
        
        when(productService.saveOne(any(Product.class))).thenReturn(newProduct);

        String productJson = objectMapper.writeValueAsString(newProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("P004"))
                .andExpect(jsonPath("$.name").value("Monitor"))
                .andExpect(jsonPath("$.description").value("4K Monitor"))
                .andExpect(jsonPath("$.price").value("499.99"));
    }

    // Test POST /api/v1/products - Create product with null ID (bad request)
    @Test
    void test_createProduct_nullId_badRequest() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setName("Invalid Product");
        invalidProduct.setDescription("Missing ID");
        invalidProduct.setPrice("99.99");

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Test POST /api/v1/products - Create product with empty ID (bad request)
    @Test
    void test_createProduct_emptyId_badRequest() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setId("");
        invalidProduct.setName("Invalid Product");
        invalidProduct.setDescription("Empty ID");
        invalidProduct.setPrice("99.99");

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Test PUT /api/v1/products/{id} - Update product (success)
    @Test
    void test_updateProduct_success() throws Exception {
        Product updatedProduct = createProduct("P001", "Updated Laptop", "Updated description", "1499.99");
        
        when(productService.updateProduct(eq("P001"), any(Product.class))).thenReturn(updatedProduct);

        String productJson = objectMapper.writeValueAsString(updatedProduct);

        mockMvc.perform(put(BASE_URI + "/P001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("P001"))
                .andExpect(jsonPath("$.name").value("Updated Laptop"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.price").value("1499.99"));
    }

    // Test PUT /api/v1/products/{id} - Update product (not found)
    @Test
    void test_updateProduct_notFound() throws Exception {
        Product product = createProduct("P999", "Non-existent", "Does not exist", "0.00");
        
        when(productService.updateProduct(eq("P999"), any(Product.class))).thenReturn(Product.EMPTY);

        String productJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(put(BASE_URI + "/P999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Test PUT /api/v1/products/{id} - Update with different ID in path and body
    @Test
    void test_updateProduct_idMismatch() throws Exception {
        // The service should use the path ID, not the body ID
        Product product = createProduct("P002", "Product", "Description", "99.99");
        Product updatedProduct = createProduct("P001", "Product", "Description", "99.99");
        
        when(productService.updateProduct(eq("P001"), any(Product.class))).thenReturn(updatedProduct);

        String productJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(put(BASE_URI + "/P001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("P001")); // Should use path ID
    }

    // Test DELETE /api/v1/products/{id} - Delete product (success)
    @Test
    void test_deleteProduct_success() throws Exception {
        when(productService.deleteProduct("P001")).thenReturn(true);

        mockMvc.perform(delete(BASE_URI + "/P001"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    // Test DELETE /api/v1/products/{id} - Delete product (not found)
    @Test
    void test_deleteProduct_notFound() throws Exception {
        when(productService.deleteProduct("P999")).thenReturn(false);

        mockMvc.perform(delete(BASE_URI + "/P999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Test DELETE /api/v1/products/{id} - Delete multiple times (idempotency check)
    @Test
    void test_deleteProduct_alreadyDeleted() throws Exception {
        // First delete succeeds
        when(productService.deleteProduct("P001")).thenReturn(true);
        mockMvc.perform(delete(BASE_URI + "/P001"))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Second delete fails (already deleted)
        when(productService.deleteProduct("P001")).thenReturn(false);
        mockMvc.perform(delete(BASE_URI + "/P001"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // ========================================================================
    // VALIDATION TESTS - Added for CRITICAL-002 fix
    // ========================================================================

    // Test POST - Invalid ID with special characters
    @Test
    void test_createProduct_invalidIdWithSpecialCharacters_badRequest() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setId("P001@#$");
        invalidProduct.setName("Valid Name");
        invalidProduct.setDescription("Valid description");
        invalidProduct.setPrice("$10.00");

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").value("Product ID can only contain alphanumeric characters, hyphens, and underscores"));
    }

    // Test POST - ID too long (exceeds 50 characters)
    @Test
    void test_createProduct_idTooLong_badRequest() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setId("P123456789012345678901234567890123456789012345678901"); // 51 chars
        invalidProduct.setName("Valid Name");
        invalidProduct.setDescription("Valid description");
        invalidProduct.setPrice("$10.00");

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").value("Product ID must be between 1 and 50 characters"));
    }

    // Test POST - Missing name (blank)
    @Test
    void test_createProduct_missingName_badRequest() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setId("P001");
        invalidProduct.setName("");
        invalidProduct.setDescription("Valid description");
        invalidProduct.setPrice("$10.00");

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Product name is required"));
    }

    // Test POST - Name too short (less than 2 characters)
    @Test
    void test_createProduct_nameTooShort_badRequest() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setId("P001");
        invalidProduct.setName("A");
        invalidProduct.setDescription("Valid description");
        invalidProduct.setPrice("$10.00");

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Product name must be between 2 and 255 characters"));
    }

    // Test POST - Name too long (exceeds 255 characters)
    @Test
    void test_createProduct_nameTooLong_badRequest() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setId("P001");
        // Create a 256 character string
        invalidProduct.setName("A".repeat(256));
        invalidProduct.setDescription("Valid description");
        invalidProduct.setPrice("$10.00");

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Product name must be between 2 and 255 characters"));
    }

    // Test POST - Description too long (exceeds 500 characters)
    @Test
    void test_createProduct_descriptionTooLong_badRequest() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setId("P001");
        invalidProduct.setName("Valid Name");
        // Create a 501 character string
        invalidProduct.setDescription("A".repeat(501));
        invalidProduct.setPrice("$10.00");

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Description must not exceed 500 characters"));
    }

    // Test POST - Invalid price format (no decimal)
    @Test
    void test_createProduct_invalidPriceFormat_badRequest() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setId("P001");
        invalidProduct.setName("Valid Name");
        invalidProduct.setDescription("Valid description");
        invalidProduct.setPrice("invalid");

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").value("Price must be in format: $10.00 or 10.00"));
    }

    // Test POST - Invalid price format (wrong decimal places)
    @Test
    void test_createProduct_invalidPriceDecimalPlaces_badRequest() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setId("P001");
        invalidProduct.setName("Valid Name");
        invalidProduct.setDescription("Valid description");
        invalidProduct.setPrice("$10.999"); // 3 decimal places

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").value("Price must be in format: $10.00 or 10.00"));
    }

    // Test POST - Valid price with dollar sign
    @Test
    void test_createProduct_validPriceWithDollarSign_success() throws Exception {
        Product validProduct = createProduct("P005", "Valid Product", "Valid description", "$99.99");
        
        when(productService.saveOne(any(Product.class))).thenReturn(validProduct);

        String productJson = objectMapper.writeValueAsString(validProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value("$99.99"));
    }

    // Test POST - Valid price without dollar sign
    @Test
    void test_createProduct_validPriceWithoutDollarSign_success() throws Exception {
        Product validProduct = createProduct("P006", "Valid Product", "Valid description", "99.99");
        
        when(productService.saveOne(any(Product.class))).thenReturn(validProduct);

        String productJson = objectMapper.writeValueAsString(validProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value("99.99"));
    }

    // Test POST - Multiple validation errors at once
    @Test
    void test_createProduct_multipleValidationErrors_badRequest() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setId(""); // Empty ID
        invalidProduct.setName("A"); // Too short
        invalidProduct.setDescription("Valid description");
        invalidProduct.setPrice("invalid"); // Invalid format

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.price").exists());
    }

    // Test POST - Valid product with null description (optional field)
    @Test
    void test_createProduct_nullDescription_success() throws Exception {
        Product validProduct = new Product();
        validProduct.setId("P007");
        validProduct.setName("Valid Name");
        validProduct.setDescription(null); // Description is optional
        validProduct.setPrice("$10.00");
        
        when(productService.saveOne(any(Product.class))).thenReturn(validProduct);

        String productJson = objectMapper.writeValueAsString(validProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    // Test POST - Valid product with null price (optional field)
    @Test
    void test_createProduct_nullPrice_success() throws Exception {
        Product validProduct = new Product();
        validProduct.setId("P008");
        validProduct.setName("Valid Name");
        validProduct.setDescription("Valid description");
        validProduct.setPrice(null); // Price is optional
        
        when(productService.saveOne(any(Product.class))).thenReturn(validProduct);

        String productJson = objectMapper.writeValueAsString(validProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    // Test PUT - Invalid ID with special characters
    @Test
    void test_updateProduct_invalidIdWithSpecialCharacters_badRequest() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setId("P001@#$");
        invalidProduct.setName("Valid Name");
        invalidProduct.setDescription("Valid description");
        invalidProduct.setPrice("$10.00");

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(put(BASE_URI + "/P001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").value("Product ID can only contain alphanumeric characters, hyphens, and underscores"));
    }

    // Test PUT - Missing name
    @Test
    void test_updateProduct_missingName_badRequest() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setId("P001");
        invalidProduct.setName("");
        invalidProduct.setDescription("Valid description");
        invalidProduct.setPrice("$10.00");

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(put(BASE_URI + "/P001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Product name is required"));
    }

    // Test PUT - Invalid price format
    @Test
    void test_updateProduct_invalidPriceFormat_badRequest() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setId("P001");
        invalidProduct.setName("Valid Name");
        invalidProduct.setDescription("Valid description");
        invalidProduct.setPrice("abc");

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(put(BASE_URI + "/P001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").value("Price must be in format: $10.00 or 10.00"));
    }

    // Test PUT - Description too long
    @Test
    void test_updateProduct_descriptionTooLong_badRequest() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setId("P001");
        invalidProduct.setName("Valid Name");
        invalidProduct.setDescription("A".repeat(501));
        invalidProduct.setPrice("$10.00");

        String productJson = objectMapper.writeValueAsString(invalidProduct);

        mockMvc.perform(put(BASE_URI + "/P001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Description must not exceed 500 characters"));
    }

    // Test PUT - Valid update with all fields
    @Test
    void test_updateProduct_validAllFields_success() throws Exception {
        Product validProduct = createProduct("P001", "Updated Product", "Updated description", "$199.99");
        
        when(productService.updateProduct(eq("P001"), any(Product.class))).thenReturn(validProduct);

        String productJson = objectMapper.writeValueAsString(validProduct);

        mockMvc.perform(put(BASE_URI + "/P001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("P001"))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.price").value("$199.99"));
    }

    // Test POST - Valid ID with hyphens and underscores
    @Test
    void test_createProduct_validIdWithHyphensAndUnderscores_success() throws Exception {
        Product validProduct = createProduct("P-001_TEST", "Valid Product", "Valid description", "$10.00");
        
        when(productService.saveOne(any(Product.class))).thenReturn(validProduct);

        String productJson = objectMapper.writeValueAsString(validProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("P-001_TEST"));
    }

    // Test POST - Minimum valid name length (2 characters)
    @Test
    void test_createProduct_minimumNameLength_success() throws Exception {
        Product validProduct = createProduct("P009", "AB", "Valid description", "$10.00");
        
        when(productService.saveOne(any(Product.class))).thenReturn(validProduct);

        String productJson = objectMapper.writeValueAsString(validProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("AB"));
    }

    // Test POST - Maximum valid description length (500 characters)
    @Test
    void test_createProduct_maximumDescriptionLength_success() throws Exception {
        Product validProduct = new Product();
        validProduct.setId("P010");
        validProduct.setName("Valid Name");
        validProduct.setDescription("A".repeat(500)); // Exactly 500 chars
        validProduct.setPrice("$10.00");
        
        when(productService.saveOne(any(Product.class))).thenReturn(validProduct);

        String productJson = objectMapper.writeValueAsString(validProduct);

        mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    /**
     * Helper method to create a Product instance
     */
    private Product createProduct(String id, String name, String description, String price) {
        return new ProductBuilder(id, name)
                .desc(description)
                .price(price)
                .build();
    }
}
