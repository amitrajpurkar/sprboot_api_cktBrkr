package com.anr.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.anr.localmdb.model.Product;
import com.anr.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all products", 
               description = "Retrieves a list of all products in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of products",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content)
    })
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get product by ID", 
               description = "Retrieves a specific product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", 
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content)
    })
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Product product = productService.findById(id);
        if (product == Product.EMPTY) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new product", 
               description = "Creates a new product in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input - validation errors", 
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content)
    })
    public ResponseEntity<?> createProduct(@Valid @RequestBody Product product, BindingResult result) {
        // Check for validation errors
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        
        Product savedProduct = productService.saveOne(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @PutMapping(value = "/{id}", 
                consumes = MediaType.APPLICATION_JSON_VALUE, 
                produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update an existing product", 
               description = "Updates an existing product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", 
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input - validation errors", 
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content)
    })
    public ResponseEntity<?> updateProduct(@PathVariable String id, 
                                           @Valid @RequestBody Product product,
                                           BindingResult result) {
        // Check for validation errors
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        
        Product updatedProduct = productService.updateProduct(id, product);
        if (updatedProduct == Product.EMPTY) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete a product", 
               description = "Deletes a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", 
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content)
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        boolean deleted = productService.deleteProduct(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
